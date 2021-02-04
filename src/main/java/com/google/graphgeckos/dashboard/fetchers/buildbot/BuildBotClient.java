// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.graphgeckos.dashboard.fetchers.buildbot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Preconditions;
import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.Log;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A single build JSON, as returned by:
 * http://lab.llvm.org:8011/api/v2/builders/clang-x86_64-debian-fast/builds?order=-number&limit=20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BuildJson {
  /** ID of this build. It uniquely identifies this build among builds performed by the builder. */
  @JsonProperty("buildid")
  int buildId;

  /**
   * The state of this build.
   *
   * <p>When the build was successful this, string is: "build successful". In case of failures, this
   * string looks like: "70 expected passes 18 expected failures 7 unsupported tests 3 unexpected
   * failures (failure)"
   */
  @JsonProperty("state_string")
  String stateString;
}

/**
 * A collection of builds, as returned by:
 * http://lab.llvm.org:8011/api/v2/builders/clang-x86_64-debian-fast/builds?order=-number&limit=20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BuildsJson {
  @JsonProperty("builds")
  BuildJson[] builds;
}

/** A change JSON, as returned by: http://lab.llvm.org:8011/api/v2/changes?limit=1 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ChangeJson {
  /** The GitHub commit hash of this change. */
  @JsonProperty("revision")
  String revision;

  /** The time when this change was committed to GitHub. */
  @JsonProperty("when_timestamp")
  long whenSecondsSinceEpoch;
}

/** A collection of changes, as returned by: http://lab.llvm.org:8011/api/v2/changes?limit=5 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ChangesJson {
  @JsonProperty("changes")
  ChangeJson[] changes;
}

/**
 * A BuildBot v2 API JSON data fetcher.
 *
 * <p>The LLVM build bots are served at: http://lab.llvm.org:8011/api/v2/.
 */
public class BuildBotClient {

  /** Provides access to the storage. */
  @Autowired private DatastoreRepository datastoreRepository;

  /**
   * Base url of the BuildBot API.
   *
   * <p>The LLVM build bot API base URL is http://lab.llvm.org:8011/api/v2.
   */
  private String baseUrl;

  /** Default object mapper used to deserialize JSON data. */
  private ObjectMapper objectMapper = new ObjectMapper();

  private static final Logger logger = Logger.getLogger(BuildBotClient.class.getName());

  public BuildBotClient(@NonNull String baseUrl, DatastoreRepository repository) {
    this.baseUrl = Preconditions.checkNotNull(baseUrl);
    this.datastoreRepository = repository;
  }

  public BuildBotClient(@NonNull String baseUrl) {
    this.baseUrl = Preconditions.checkNotNull(baseUrl);
  }

  /**
   * Returns the text of a HTTP GET request to the base URL.
   *
   * The endpoint is computed from the arguments via {@code String.format}.
   * 
   * @param format format string for the HTTP endpoint relative to the base URL.
   * @param args the args to substitute in the format string.
   */
  private String httpGet(String format, Object... args) {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .build()
        .get()
        .uri(String.format(format, args))
        .accept(MediaType.TEXT_PLAIN)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  /** Returns the latest 20 builds for {@code builderId}, or null if they cannot be retrieved. */
  private BuildJson[] getLatestBuilds(String builderId) {
    String response;
    try {
      response = httpGet("/builders/%s/builds?order=-buildid&limit=20", builderId);
    } catch (Exception e) {
      logger.warning(
          String.format("Failed to fetch latest builds for builderId: %s: %s", builderId, e));
      return null;
    }
    if (response == null) {
      logger.warning(String.format("Failed to get build results for builderId: %s", builderId));
      return null;
    }
    try {
      BuildsJson builds = objectMapper.readValue(response, BuildsJson.class);
      if (builds == null) return null;
      return builds.builds;
    } catch (JsonProcessingException e) {
      logger.severe(String.format("Failed to deserialize GET response %s: %s", response, e));
    }
    return null;
  }

  /**
   * Returns the change of the build identified by {@code buildId}, or null if the build is not
   * found.
   */
  private ChangeJson getChange(int buildId) {
    String response = httpGet("/builds/%d/changes?limit=1", buildId);
    try {
      ChangesJson changes = objectMapper.readValue(response, ChangesJson.class);
      if (changes != null && changes.changes != null && changes.changes.length > 0) {
        return changes.changes[0];
      }
    } catch (JsonProcessingException e) {
      logger.severe(String.format("Failed to deserialize GET response %s: %s", response, e));
    }
    return null;
  }

  /**
   * Fetches build information for the {@code buildBot}.
   * Starts fetching process. Fetches data every {@code delay} seconds from the url: {@code
   * baseUrl}/{@code buildBot}/builds/{@code buildId}. E.g with buildBot =
   * "clang-x86_64-debian-fast" and buildId = 1000, baseUrl =
   * "http://lab.llvm.org:8011/json/builders" the request url will be
   * http://lab.llvm.org:8011/json/builders/clang-x86_64-debian-fast/builds/1000 Adds valid fetched
   * data in the form of {@link BuildBotData} to the storage and tries to fetch an entry with the
   * next buildId after waiting for {@code delay} seconds. If the fetched data is empty (invalid)
   * waits for {@code delay} seconds and tries to make the same request.
   *
   * @param buildBot name of the BuildBot as it is in the API (e.g "clang-x86_64-debian-fast")
   * @param initialBuildId the id of the BuildBot's build from where to start fetching data
   */
  public void run(@NonNull String buildBot) {
    Preconditions.checkNotNull(buildBot);

    BuildJson[] builds = getLatestBuilds(buildBot);
    logger.info("builds: " + builds);
    if (builds == null) return;
    for (BuildJson build : builds) {
      ChangeJson change = getChange(build.buildId);
      logger.info("change: " + change);
      if (change == null) continue;
      BuildBotData data =
          new BuildBotData(change.revision, buildBot, new ArrayList<Log>(), BuilderStatus.PASSED);
      data.setTimestamp(Timestamp.ofTimeSecondsAndNanos(change.whenSecondsSinceEpoch, 0));
      logger.info("Updating: " + data);
      datastoreRepository.updateRevisionEntry(data);
    }
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
}
