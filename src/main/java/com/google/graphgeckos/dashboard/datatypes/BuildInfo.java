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

  /**
   * The commit hash of the associated revision.
   */
  @Id
  @Field(name = "commitHash")
  private String commitHash;

  /**
   * Timestamp when the commit was pushed.
   */
  @Field(name = "timestamp")
  private Timestamp timestamp;

  /**
   * Branch where the commit was pushed.
   */
  @Unindexed
  @Field(name = "branch")
  private String branch;

  /**
   * List of buildbots which attempted compilation and their results.
   */
  @Unindexed
  @Field(name = "builders")
  private List<BuildBotData> builders;

  /**
   * The global compilation status of the revision.
   */
  @Unindexed
  @Field(name = "status")
  private RevisionStatus status;

  /**
   * Used by Spring GCP.
   */
  public BuildInfo() {}

  /**
   * Converts a {@link GitHubData} object to a BuildInfo object.
   * This is used for adding entries to the Google Cloud Datastore, from the Git commit
   * information received, and leaving the {@code builders} field empty, for later updates.
   */
  public BuildInfo(GitHubData creationData) {
    this.commitHash = creationData.getCommitHash();
    this.timestamp = Timestamp.parseTimestamp(creationData.getTimestamp());
    this.branch = creationData.getBranch();
    this.builders = new ArrayList<>();
    this.status = RevisionStatus.lost;
  }

  @NonNull
  public String getCommitHash() {
    return commitHash;
  }

  @NonNull
  public Timestamp getTimestamp() {
    return timestamp;
  }

  @NonNull
  public String getBranch() {
    return branch;
  }

  @NonNull
  public List<BuildBotData> getBuilders() {
    return builders;
  }

  @NonNull
  public RevisionStatus getStatus() {
    return status;
  }

  public void setCommitHash(@NonNull String commitHash) {
    this.commitHash = commitHash;
  }

  public void setTimestamp(@NonNull Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public void setBranch(@NonNull String branch) {
    this.branch = branch;
  }

  public void setBuilders(@NonNull List<BuildBotData> builders) {
    this.builders = new ArrayList<>(builders);
  }

  public void addBuilder(@NonNull BuildBotData update) {
    builders.add(update);
  }

  /**
   * Updates the {@code status} field according to all the aggregated builder statuses.
   * If there is no builder data, or all builder data are lost, the status will be {@code lost}.
   */
  void reanalyseStatus() {
    boolean hasPassed = false;
  
    for (BuildBotData builder : builders) {
      if (builder.getStatus() == BuilderStatus.FAILED) {
        this.status = RevisionStatus.failed;

        return;
      } else if (builder.getStatus() == BuilderStatus.PASSED) {
        hasPassed = true;
      }
    }
  
    if (hasPassed) {
      this.status = RevisionStatus.passed;
    } else {
      this.status = RevisionStatus.lost;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof BuildInfo)) {
      return false;
    }
    BuildInfo other = (BuildInfo) o;
    return commitHash.equals(other.commitHash) && timestamp.equals(other.timestamp);
  }

}
