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
import java.util.Date;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
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
  private final String commitHash;

  @Field(name = "timestamp")
  private final Timestamp timestamp;

  @Field(name = "branch")
  private final String branch;

  @Field(name = "builders")
  @Unindexed
  private final List<Builder> builders;

  /**
   * Converts a {@link #ParsedGitData ParsedGitData} object to a BuildInfo object.
   * This is used for adding entries to the Google Cloud Datastore, from the Git commit
   * information received, and leaving the {@code builders} field as empty, for later updates.
   */
  BuildInfo(ParsedGitData creationData) {
    this.commitHash = creationData.getCommitHash();
    this.timestamp = creationData.getTimestamp();
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
  public List<Builder> getBuilders() {
    return builders;
  }

}
