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

package com.google.graphgeckos.dashboard.storage;

import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Unindexed;
import org.springframework.data.annotation.Id;

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

  @Field(name = "branch")
  @Unindexed
  private String branch;

  @Field(name = "builders")
  @Unindexed
  private List<Builder> builders;

  BuildInfo() {
    commitHash = null;
    timestamp = null;
    branch = null;
    builders = null;
  }

  /**
   * Converts a {@link #ParsedGitData ParsedGitData} object to a BuildInfo object.
   * This is used for adding entries to the Google Cloud Datastore, from the Git commit
   * information received, and leaving the {@code builders} field empty, for later updates.
   */
  BuildInfo(ParsedGitData creationData) {
    this.commitHash = creationData.getCommitHash();
    this.timestamp = creationData.getTimestamp();
    this.branch = creationData.getBranch();
    this.builders = new ArrayList<>();
  }

  BuildInfo(String commitHash, Timestamp timestamp, String branch, List<Builder> builders) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.branch = branch;
    this.builders = new ArrayList<>(builders);
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
  public List<Builder> getBuilders() {
    return builders;
  }

  void setCommitHash(String commitHash) {
    this.commitHash = commitHash;
  }

  void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  void setBranch(String branch) {
    this.branch = branch;
  }

  void setBuilders(List<Builder> builders) {
    this.builders = new ArrayList<>(builders);
  }

  void addBuilder(Builder update) {
    builders.add(update);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
  
    if (!(o instanceof BuildInfo)) {
      return false;
    }
    
    BuildInfo other = (BuildInfo) o;
    return this.commitHash.equals(other.commitHash) && this.timestamp.equals(other.timestamp) &&
           this.branch.equals(other.branch) && this.builders.equals(other.builders);
  }

}
