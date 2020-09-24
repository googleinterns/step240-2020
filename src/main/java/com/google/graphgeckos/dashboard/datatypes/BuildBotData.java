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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Transient;
import org.springframework.lang.NonNull;

/**
 * Contains the information retrieved from a single build bot. It is used as a member
 * of {@link BuildInfo}
 */
@Entity(name = "builder")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildBotData {

  /**
   * The commit hash, it is fetched from the BuildBot API.
   * This field is transient because it is only needed to
   * determine which commit this data belongs to and put
   * it in the right {@link BuildInfo} in the storage.
   * Used in {@link com.google.graphgeckos.dashboard.storage.DatastoreRepository}.
   */
  @Transient
  @Field(name = "commitHash")
  private String commitHash;

  /** 
   * The timestamp of the build. 
   */
  @Field(name = "timestamp")
  private Timestamp timestamp;

  /**
   * Name of the buildbot.
   */
  @Field(name = "name")
  private String name;

  /**
   * Step data for each individual stage of compilation. See {@link
   *      com.google.graphgeckos.dashboard.storage.BuildStep BuildStep}
   */
  @Field(name = "steps")
  private List<BuildStep> buildSteps = new ArrayList<>();

  /**
   * Builder compilation status, as described by {@link BuilderStatus}.
   */
  @Field(name = "status")
  private BuilderStatus status = BuilderStatus.FAILED;

  /**
   * Only used by Spring GCP.
   */
  public BuildBotData() {}

  public BuildBotData(String commitHash, String name, List<BuildStep> buildSteps, BuilderStatus status) {
    this.commitHash = commitHash;
    this.name = name;
    this.buildSteps.addAll(buildSteps);
    this.status = status;
  }

  public BuildBotData(@JsonProperty("builderName") String name) {
    this.name = name;
  }

  /**
   * Extracts nested commitHash("revision"), branch("branch") and
   * timestamp("when") fields from the json
   *
   * @param sourceStamp Representation of the json component, where the commitHash field is located
   */
  @JsonProperty("sourceStamp")
  public void unpackSourceStamp(Map<String, Object> sourceStamp) {
    commitHash = sourceStamp.get("revision").toString();
    List<?> changes = (List<?>) sourceStamp.get("changes");
    Map<?, ?> latestChange = (Map<?, ?>) changes.get(0);
    timestamp = Timestamp.ofTimeMicroseconds(Long.parseLong(latestChange.get("when").toString()));
  }

  /**
   * Defines builder status based on a phrase provided in parsed json.
   * If something failed or the phrase doesn't contain any of the key words
   * ("failed", "successful", "lost"), then the buildbot is considered failed {@code FAILED}.
   * If something is lost, then the buildbot is considered lost {@code LOST}.
   * If  the buildbot is considered passed {@code PASSED}.
   *
   * @param words Words of the "text" JSON field
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
   *
   * @param logs Representation of the json component, where the logs are located
   */
  @JsonProperty("steps")
  private void unpackSteps(List<Map<String, ?>> steps) {
    for(Map<String, ?> step : steps) {

      int stepNumber = (int) step.get("step_number");

      List<?> textList = (List<?>) step.get("text");
      String text = "";
      StringBuilder sb = new StringBuilder();
      textList.forEach(t -> sb.append(t).append(' '));
      text = sb.toString();

      String name = step.get("name").toString();
      boolean isFinished = (boolean) step.get("isFinished");
      boolean isStarted = (boolean) step.get("isStarted");

      List<?> logList = (List<?>) step.get("logs");
      List<Log> logs = new ArrayList<Log>();
      logList.forEach(log -> {
        List<?> listlog = (List<?>) log;
        logs.add(new Log(listlog.get(0).toString(), listlog.get(1).toString()));
      });

      this.buildSteps.add(new BuildStep(
        stepNumber, name, text, isFinished, isStarted, logs
      ));
    }
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public List<BuildStep> getSteps() {
    return buildSteps;
  }

  @NonNull
  public BuilderStatus getStatus() {
    return status;
  }

  @NonNull
  public String getCommitHash() {
    return commitHash;
  }

  @NonNull
  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  public void setSteps(@NonNull List<BuildStep> buildSteps) {
    this.buildSteps = new ArrayList<>(buildSteps);
  }

  public void setStatus(@NonNull BuilderStatus status) {
    this.status = status;
  }

  public void setCommitHash(@NonNull String commitHash) {
    this.commitHash = commitHash;
  }

  public void setTimestamp(@NonNull Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Checks if all non-transient fields are equal between BuildBotData instances.
   */
  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof BuildBotData)) {
      return false;
    }

    BuildBotData other = (BuildBotData) o;
    return timestamp.equals(other.timestamp) && name.equals(other.name) &&
           buildSteps.equals(other.buildSteps) && status.equals(other.status);
  }

}
