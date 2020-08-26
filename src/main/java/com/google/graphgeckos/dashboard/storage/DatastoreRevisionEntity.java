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

/**
 * An intermediary container used by the Spring - Google Cloud Datastore integration,
 * allowing easy creation/update of new entities in the database. It is created using
 * a commit's metadata from git, and updated using
 * {@link com.google.graphgeckos.dashboard.storage#Builder Builder} objects, which add
 * compilation information from each buildbot. The scope of an instance is supposed to
 * be contained inside a single method from DataRepository.
 */
@Entity(name = "revision")
class DatastoreRevisionEntity {
  @Id
  @Field(name = "commitHash")
  private String commitHash;
  
  @Field(name = "index")
  private int index;

  @Field(name = "timestamp")
  private Timestamp timestamp;

  @Field(name = "branch")
  private String branch;

  @Field(name = "builders")
  private List<Builder> builders;

  /**
   * Constructs a DatastoreRevisionEntity from the raw commit information from git,
   * and an index used for database ordering.
   */
  DatastoreRevisionEntity(ParsedGitData creationData, int index) {
    this.commitHash = creationData.getCommitHash();
    this.index = index;
    this.timestamp = creationData.getTimestamp();
    this.branch = creationData.getBranch();
    this.builders = new ArrayList<>();
  }

  /**
   * Getter for the commitHash.
   *
   * @return the commitHash.
   */
  String getCommitHash() {
    return commitHash;
  }

  /**
   * Getter for the timestamp.
   *
   * @return the timestamp.
   */
  Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Getter for the branch.
   *
   * @return the branch.
   */
  String getBranch() {
    return branch;
  }

  /**
   * Getter for the builders.
   *
   * @return the builders.
   */
  List<Builder> getBuilders() {
    return builders;
  }

  /**
   * Setter for the index. Used in the reindexing operation inside GCDataRepository.
   */
  void setIndex(int newIndex) {
    index = newIndex;
  }

  /**
   * Adds a new builder to the builders list.
   */
  void addBuilder(Builder newBuilder) {
    builders.add(newBuilder);
  }
}
