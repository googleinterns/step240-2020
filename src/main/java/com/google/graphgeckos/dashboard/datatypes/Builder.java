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

package com.google.graphgeckos.dashboard.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Transient;

/**
 * Contains the information retrieved from a single build bot. It is used as a member of BuildInfo...
 * of {@link BuildInfo}
 */
@Entity(name = "builder")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Builder {

  @Transient
  private String commitHash;

  @Transient
  private String branch;

  /* The timestamp of the build. */
  private String timestamp;

  /* Name of the buildbot. */
  private final String name;

  /**
   * The logs of each compilation stage, stored as described by {@link Log}.
   */
  private final List<Log> logs = new ArrayList<>();

  /**
   * Builder compilation status, as described by {@link BuilderStatus}.
   */
  private BuilderStatus status;

  /**
   * Extracts nested commitHash("revision"), branch("branch") and timestamp("when")
   * fields from the json.
   * @param sourceStamp Representation of the json component,
   *                    where the commitHash and the branch fields is located.
   */
  @JsonProperty("sourceStamp")
  public void unpackSourceStamp(Map<String, Object> sourceStamp) {
    commitHash = sourceStamp.get("revision").toString();
    branch = sourceStamp.get("branch").toString();
    timestamp = sourceStamp.get("when").toString();
  }

  /**
   * Defines builder status based on a phrase provided in parsed json.
   * If something failed, then the buidbot is considered as failed {@code FAILED}.
   * If something lost, then the buildbot is considered lost {@code LOST}.
   * Otherwise the buildbot is considered passed {@code PASSED}.
   * @param words Words of the "text" json field.
   */
  @JsonProperty("text")
  private void extractStatus(List<String> words) {
    for (String word : words) {
      if (word.equals("failed")) {
        status = BuilderStatus.FAILED;
        break;
      }
      if (word.equals("lost")) {
        status = BuilderStatus.LOST;
        break;
      }
      if (word.equals("successful")) {
        status = BuilderStatus.PASSED;
      }
    }
  }

  /**
   * Unpacks logs represented as list of lists of two strings,
   * where the first one is a type of the log and the second one
   * is a link to the log.
   * @param logs Representation of the json component, where the logs are located.
   */
  @JsonProperty("logs")
  private void unpackLogs(List<String[]> logs) {
    logs.forEach(x -> this.logs.add(new Log(x)));
  }

  public Builder(@JsonProperty("builderName") String name) {
    this.name = name;
  }

  /**
   * Returns the name of the builder bot. Cannot be null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the list of logs for each compilation stage. Cannot be null.
   */
  public List<Log> getLogs() {
    return logs;
  }

  /**
   * Returns the compilation status of the builder. Cannot be null.
   */
  public BuilderStatus getStatus() {
    return status;
  }

  /**
   * Returns the commit hash of the commit tested by the current buildbot.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Returns the branch of the commit tested by the current buildbot.
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Returns the timestamp of the build.
   */
  public String getTimestamp() {
    return timestamp;
  }

}
