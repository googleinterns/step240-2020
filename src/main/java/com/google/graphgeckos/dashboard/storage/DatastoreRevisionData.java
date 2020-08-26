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
import java.util.List;
import java.util.ArrayList;

/**
 * An immutable container used for providing stripped-down git information
 * to the GCDataRepository. Provides functionality for checking whether the information
 * is sufficient for creating an entry. Does not modify or validity check the individual
 * contents of each field.
 *
 * Fields included: 
 * -commitHash: the commit hash of the revision built by a certain build bot
 * -timestamp: the formatted date when the commit was pushed
 * -branch: the branch of the LLVM project on which this commit was pushed
 */
@Entity(name = "revision")
class DatastoreRevisionData {
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

  DatastoreRevisionData(ParsedGitData creationData, int index) {
    this.commitHash = creationData.getCommitHash();
    this.index = index;
    this.timestamp = creationData.getTimestamp();
    this.branch = creationData.getBranch();
    this.builders = new ArrayList<>();
  }

  String getCommitHash() {
    return commitHash;
  }

  Timestamp getTimestamp() {
    return timestamp;
  }

  String getBranch() {
    return branch;
  }

  List<Builder> getBuilders() {
    return builders;
  }

  void setIndex(int newIndex) {
    index = newIndex;
  }

  void addBuilder(Builder newBuilder) {
    builders.add(newBuilder);
  }
}
