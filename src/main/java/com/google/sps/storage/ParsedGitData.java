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

package com.google.sps.storage;

/**
 * An immutable container used for providing stripped-down git information
 * to the StorageController. Provides functionality for checking whether the information
 * is sufficient for creating an entry. Does not modify or validity check the individual
 * contents of each field.
 */
public class ParsedBuildbotData {
  private final String commitHash;
  private final String timestamp;
  private final String branch;

  /**
   * Creates an instance with information for creating a database entry.
   */
  public ParsedBuildbotData(final String commitHash, final String timestamp, final String branch) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.branch = branch;
  }

  /**
   * Checks for the existance of the fields necessary for creating a database entry.
   * Does not check the validity of each field.
   */
  boolean validCreateData() {
    return commitHash != null;
  }

  String getCommitHash() {
    return commitHash;
  }

  String getTimestamp() {
    return timestamp;
  }

  List<String> getBranch() {
    return branch;
  }
}
