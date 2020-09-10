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

import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Transient;
import org.springframework.lang.NonNull;

/**
 * Contains the information retrieved from a single build bot. It is used as a member
 * of {@link BuildInfo}
 */
@Entity(name = "builder")
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
   * The logs of each compilation stage, stored as described by {@link Log}.
   */
  @Field(name = "logs")
  private final List<Log> logs = new ArrayList<>();

  /**
   * Builder compilation status, as described by {@link BuilderStatus}.
   */
  @Field(name = "status")
  private BuilderStatus status;

  /**
   * Only used by Spring GCP.
   */
  public BuildBotData() {}

  public BuildBotData(String commitHash, String name, List<Log> logs, BuilderStatus status) {
    this.commitHash = commitHash;
    this.name = name;
    this.logs.addAll(logs);
    this.status = status;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public List<Log> getLogs() {
    return logs;
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

  public void setLogs(@NonNull List<Log> logs) {
    this.logs = new ArrayList<>(logs);
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

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof BuildBotData)) {
      return false;
    }

    BuildBotData other = (BuildBotData) o;
    return commitHash.equals(other.commitHash) && timestamp.equals(other.timestamp) &&
           name.equals(other.name) && logs.equals(other.logs) && status.equals(other.status);
  }

}
