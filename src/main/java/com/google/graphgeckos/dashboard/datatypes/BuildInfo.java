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
import java.util.Date;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Unindexed;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

/**
 * This class encapsulates all the information gathered about a specific revision.
 * Information included: 
 *    * commit hash
 *    * timestamp
 *    * branch
 *    * information from builders:
 *          - builder name
 *          - compile logs
 *          - compilation status
 *
 * This class is used to facilitate Datastore I/O operations through Spring Cloud API,
 * and to transfer the data from the DataRepository to the REST API component.
 */
@Entity(name = "revision")
public class BuildInfo {

  @Id
  @Field(name = "commitHash")
  private String commitHash;

  @Field(name = "timestamp")
  private Timestamp timestamp;

  @Unindexed
  @Field(name = "branch")
  private String branch;

  @Unindexed
  @Field(name = "builders")
  private List<BuildBotData> builders;

  public BuildInfo() { }

  /**
   * Converts a {@link GitHubData} object to a BuildInfo object.
   * This is used for adding entries to the Google Cloud Datastore, from the Git commit
   * information received, and leaving the {@code builders} field empty, for later updates.
   */
  public BuildInfo(GitHubData creationData) {
    this.commitHash = creationData.getCommitHash();
    this.timestamp = Timestamp.of(new Date(creationData.getTimestamp()));
    this.branch = creationData.getBranch();
    this.builders = new ArrayList<>();
  }

  /**
   * Getter for the Git commit hash of the associated revision. Cannot be null.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Returns time of when the commit was pushed as a {@link com.google.cloud#Timestamp
   * Timestamp} value. Cannot be null.
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Returns name of the Git branch where the revision was pushed. Cannot be null.
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Returns builders which attempted the compilation of the revision. Cannot be null, and
   * neither it's elements.
   */
  public List<BuildBotData> getBuilders() {
    return builders;
  }

  /**
   * Sets the Git commit hash of the revision. Should not be null.
   */
  public void setCommitHash(@NonNull String commitHash) {
    this.commitHash = commitHash;
  }

  /**
   * Sets the timestamp of the build. Should not be null.
   */ 
  public void setTimestamp(@NonNull Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Sets the branch where the revision was pushed. Should not be null.
   */
  public void setBranch(@NonNull String branch) {
    this.branch = branch;
  }

  /**
   * Sets the list of builders which attempted compilation of the revision. Should not be null.
   */
  public void setBuilders(@NonNull List<BuildBotData> builders) {
    this.builders = new ArrayList<>(builders);
  }

  /**
   * Adds a BuildBotData to the list of builders which attempted compilation of the revision.
   * Should not be null.
   */
  public void addBuilder(@NonNull BuildBotData update) {
    builders.add(update);
  }

  public boolean equals(BuildInfo other) {
    return this.commitHash == other.commitHash && this.timestamp.equals(other.timestamp) &&
           this.branch == other.branch && this.builders.equals(other.builders);
  }

}
