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

/**
 * An immutable container used for providing stripped-down build bot information
 * to the StorageController. Provides functionality for checking whether the information
 * is sufficient for an update of an entry. Does not modify or validity check the individual
 * contents of each field.
 *
 * Fields included: 
 * -commitHash: the commit hash of the revision built by a certain build bot.
 * -builderName: the name of the build bot which performed the compilation.
 * -logs: name of the stage and links to the logs for each stage of the compilation.
 * -status: the final result of the compilation (passed or failed).
 */
public class ParsedBuildbotData {
  private final String commitHash;
  private final String builderName;
  private final List<String> logs;
  private final boolean status;

  /**
   * Constructs an immutable instance of a ParsedBuildbotData.
   *
   * @param commitHash the commit hash of the revision this data refers to.
   * @param builderName the name of the builder which performed the compilation.
   * @param logs logs for each individual stage of compilation.
   * @param status the results of the compilation (True for passed, False for failed).
   */
  public ParsedBuildbotData(final String commitHash, final String builderName,
                            final List<String> logs, final boolean status) {
    this.commitHash = commitHash;
    this.builderName = builderName;
    this.logs = new LinkedList<>(logs);
    this.status = status;
  }

  /**
   * Checks for the existance of the fields necessary for updating a database entry.
   * Does not check the validity of each field.
   *
   * @return true if all required fields are available, false if not.
   */
  boolean validUpdateData() {
    return (commitHash != null && builderName != null && logs != null);
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
   * Getter for the builderName.
   *
   * @return the builderName.
   */
  String getBuilderName() {
    return builderName;
  }

  /**
   * Getter for the logs.
   *
   * @return the logs.
   */
  List<String> getLogs() {
    return logs;
  }

  /**
   * Getter for the status.
   *
   * @return the status.
   */
  boolean getStatus() {
    return status;
  }
}
