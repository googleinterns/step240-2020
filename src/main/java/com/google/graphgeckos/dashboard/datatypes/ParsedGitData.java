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
public class ParsedGitData {
  private final String commitHash;
  private final Timestamp timestamp;
  private final String branch;

  /**
   * Constructs an immutable instance of the ParsedGitData.
   *
   * @param commitHash the commit hash of the revision this data refers to
   * @param timestamp the time when the commit was pushed
   * @param branch branch of the LLVM project on which this commit was pushed
   * @throws IllegalArgumentException if commitHash is null
   */
  public ParsedGitData(String commitHash, Timestamp timestamp, String branch)
                                                                throws IllegalArgumentException {
    if (commitHash == null) {
      throw new IllegalArgumentException("ParsedGitData's commitHash cannot be null");
    }

    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.branch = branch;
  }

  /**
   * Getter for the commitHash.
   *
   * @return the commitHash.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Getter for the timestamp.
   *
   * @return the timestamp.
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Getter for the branch.
   *
   * @return the branch.
   */
  public String getBranch() {
    return branch;
  }
}
