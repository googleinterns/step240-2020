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

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
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
   * @throws IllegalArgumentException if entryData is null
   */
  boolean createRevisionEntry(GitHubData entryData) throws IllegalArgumentException;

  /**
   * Updates an existing revision's database entry, with the individual information from
   * a particular buildbot. If there is no entry associated with the provided commit hash,
   * ignores the request.
   *
   * @param updateData a BuildBotData instance, must have a non-null "commitHash" field
   * @return true only if the operation completed successfully.
   * @throws IllegalArgumentException if updateData is null.
   */
  boolean updateRevisionEntry(BuildBotData updateData) throws IllegalArgumentException;

  /**
   * Deletes a revision's database entry, based on it's commit hash. Has no effect if there
   * is no Entity associated to the commit hash.
   *
   * @param commitHash the String representation of the commit hash of the revision data to
   * be deleted
   * @return true only if the operation completed successfully.
   * @throws IllegalArgumentException if commitHash is null
   */
  boolean deleteRevisionEntry(String commitHash) throws IllegalArgumentException;

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
   * @throws IllegalArgumentException if commitHash is null
   */
  BuildInfo getRevisionEntry(String commitHash) throws IllegalArgumentException;

  /**
   * Queries the database for the last known index for a specific buildbot. Used by the fetchers.
   *
   * @param buildbotName the name of the buildbot to search for
   * @return the last known revision index for which there is information in the database.
   * @throws IllegalArgumentException if {@code buildbotName} is null
   * @throws BuildbotNotFound if there is no "index" entry related to the name provided
   */
  int getBuildbotIndex(String buildbotName);

  /**
   * Updates the database with a new value for the internal buildbot revision index. Used by the
   * fetchers.
   *
   * @param buildbotName the name of the buildbot to update
   * @param newValue the new index value
   * @throws IllegalArgumentException if {@code buildbotName} is null or there is no "index" entry
   *      with that key
   * @throws IndexOutOfBoundsException if newValue is lower or equal than the previous
   *      recorded value
   */
  void setBuildbotIndex(String buildbotName, int newValue);

  /**
   * Creates a new "index" entry if there is none with the same name in the database. This should
   * be called once before setting the index for a buildbot that has no previous entry. If the
   * value provided is larger than the previous value, the call updates the value.
   *
   * @param name the name of the buildbot to register
   * @param value the starting index value of the buildbot
   * @throws IllegalArgumentException if {@code name} is null
   * @throws IndexOutOfBoundsException if value is negative when creating a new entry, or if value
   *      is lower or equal than the previous recorded value
   */
  void registerNewBuildbot(String buildbotName, int value);
}
