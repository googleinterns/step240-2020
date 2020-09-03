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

import com.google.graphgeckos.dashboard.components.BuildInfo;
import com.google.graphgeckos.dashboard.components.Builder;
import com.google.graphgeckos.dashboard.components.GitHubData;

import java.util.List;

/**
 * A storage component for creating/updating/deleting revision data, with the possibility
 * of querying entries in bulk, sorted by their timestamp.
 */
public interface DataRepository {
  /**
   * Creates a new database entry of the "revision" type with the metadata of the revision.
   * If there already is an entry with the same commit hash, ignores the request.
   *
   * @param entryData a ParsedGitData instance, must have a non-null "commitHash" field
   * @return true only if the operation completed successfully.
   */
  boolean createRevisionEntry(GitHubData entryData);

  /**
   * Updates an existing revision's database entry, with the individual information from
   * a particular buildbot. If there is no entry associated with the provided commit hash,
   * ignores the request.
   *
   * @param updateData a ParsedBuildbotData instance, must have a non-null "commitHash" field
   * @return true only if the operation completed successfully.
   */
  boolean updateRevisionEntry(Builder updateData);

  /**
   * Deletes a revision's database entry, based on it's commit hash. Has no effect if there
   * is no Entity associated to the commit hash.
   *
   * @param commitHash the String representation of the commit hash of the revision data to
   * be deleted
   * @return true only if the operation completed successfully.
   */
  boolean deleteRevisionEntry(String commitHash);

  /**
   * Queries the database for a specified amount of entries of type "revision", going down
   * in chronological order, starting from an offset from the latest.
   *
   * @param number the number of database entries to retrieve
   * @param offset the offset from the latest database entry, for which to consider
   *     the requested number of entries
   * @return a list containing at most {@code number} entries starting from the latest
   *     entry - {@code offset}. If the database has not enough entries for the requested
   *     {@code offset} and {@code number}, returns all the available entries from that range.
   * @throws IllegalArgumentException if either number or offset are < 0
   */
  List<BuildInfo> getLastRevisionEntries(int number, int offset) throws IllegalArgumentException;

  /**
   * Queries the database for a given entry, that has the primary key set
   * to the provided commitHash.
   *
   * @param commitHash the commitHash to search for
   * @return null if no object was found, else a BuildInfo instance of the database entry
   *     associated to that commitHash.
   */
  BuildInfo getRevisionEntry(String commitHash);

  /**
   * Checks if the given entry is present.
   *
   * @param commitHash the commitHash to search for.
   * @return false if no object was found, else returns true.
   */
  default boolean hasRevisionEntry(String commitHash) {
    return getRevisionEntry(commitHash) != null;
  }

}
