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
 * An interface for providing unified I/O to the storage component.
 */
public interface DataRepository {
  /**
   * Creates a new database entry about a te metadata of a commit. If there already
   * is an entry with the same commit hash, ignores the request.
   *
   * @param entryData a ParsedGitData instance, must have a non-null "commitHash" field.
   * @throws IllegalArgumentException if entryData is null or doesn't have a
   *      "commitHash" field.
   */
  void createEntry(ParsedGitData entryData);

  /**
   * Updates an existing database entry, with the individual information from
   * a particular buildbot. If there is no entry associated with the provided commit hash,
   * ignores the request.
   *
   * @param updateData a ParsedBuildbotData instance, must have a non-null "commitHash" field.
   * @throws IllegalArgumentException if entryData is null or doesn't have a
   *     "commitHash" field.
   */
  void updateEntry(ParsedBuildbotData updateData);

  /**
   * Deletes a database entry which has its commit hash equal to the one provided in the
   * arguments. Has no effect if there is no Entity associated to the commit hash.
   *
   * @param commitHash the String representation of the commit hash of the revision data to
   * be deleted.
   */
  void deleteEntry(String commitHash);

  /**
   * Queries the database for a specified amount of entries, going down in chronological
   * order, starting from an offset.
   *
   * @param number is the number of database entries to retrieve.
   * @param offset is the offset from the latest database entry, for which to consider
   *     the requested number of entries.
   * @return an iterable object containing at most {@code number} entries starting
   *     from the latest entry - {@code offset}. If the database has not enough entries
   *     for the requested {@code offset} and {@code number}, returns all the available
   *     entries from that range.
   * @throws IllegalArgumentException if either number or offset are < 0.
   */
  Iterable<AggregatedBuildbotData> getLastEntries(int number, int offset)
                                                  throws IllegalArgumentException;
}
