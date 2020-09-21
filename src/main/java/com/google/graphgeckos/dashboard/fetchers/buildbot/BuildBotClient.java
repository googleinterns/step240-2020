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

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/** A (external) BuildBot API json data fetcher. */
public class BuildBotClient {

  /** Provides access to the storage. */
  @Autowired
  private DatastoreRepository datastoreRepository;

  /** Base url of the BuildBot API. */
  private String baseUrl;

  private static final Logger logger = Logger.getLogger(BuildBotClient.class.getName());

  public BuildBotClient(@NonNull String baseUrl) {
    Objects.requireNonNull(baseUrl);
    this.baseUrl = baseUrl;
  }

  /**
   * Starts fetching process. Fetches data every {@code delay} seconds from the url:
   * {@code baseUrl}/{@code buildBot}/builds/{@code buildId}?as_text=1.
   * E.g with buildBot = "clang-x86_64-debian-fast" and buildId = 1000,
   * baseUrl = "http://lab.llvm.org:8011/json/builders" the request url will be
   * http://lab.llvm.org:8011/json/builders/clang-x86_64-debian-fast/builds/1000?as_text=1 .
   * Adds valid fetched data in the form of {@link BuildBotData} to the storage and tries
   * to fetch an entry with the next buildId after waiting for {@code delay} seconds.
   * If the fetched data is empty (invalid) waits for {@code delay} seconds and tries
   * to make the same request.
   *
   * @param buildBot name of the BuildBot as it is in the API (e.g "clang-x86_64-debian-fast")
   * @param initialBuildId the id of the BuildBot's build from where to start fetching data
   */
  public void run(@NonNull String buildBot, long initialBuildId, long delay) {
    Objects.requireNonNull(buildBot);

    AtomicLong buildId = new AtomicLong(initialBuildId);
    logger.info("Started fetching");
    WebClient.builder().baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .build()
      .get()
      .uri(String.format( "/%s/builds/%d?as_text=1", buildBot, buildId.get()))
      .accept(MediaType.TEXT_PLAIN)
      .retrieve()
      .bodyToMono(String.class)
      .delaySubscription(Duration.ofSeconds(delay))
      .onErrorResume(e -> {
        logger.info("Ignoring error: " + e.getMessage());
        return Mono.empty();
      })
      .repeat()
      .subscribe(response -> {
        if (response.isEmpty()) {
          logger.info(String.format("Error occured, waiting in %d seconds", delay));
          return;
        }
        logger.info("Got valid json, will deserialize it.");
        try {
          BuildBotData builder = new ObjectMapper().readValue(response, BuildBotData.class);
          datastoreRepository.updateRevisionEntry(builder);
        } catch (Exception e) {
          logger.info("Unable to deserialize: " + response);
          e.printStackTrace();
        }
        long nextBuildId = buildId.incrementAndGet();
        logger.info(
               String.format("Next build id is %d, performing request in %d seconds", nextBuildId, delay));
      });
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

}