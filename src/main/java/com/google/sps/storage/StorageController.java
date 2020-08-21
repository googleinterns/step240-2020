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
 * An interface for providing unified I/O to the storage component.
 */
public interface StorageController {
  /**
   * Creates a new database entry, with just the metadata of a commit,
   * having the passed/failed fields empty.
   *
   * @param entryData a JSON in plain string format, must have a "commitHash" field.
   * @throws IllegalArgumentException if entryData is null or doesn't have a
   *      "commitHash" field.
   */
  void createNewEntry(final String entryData);

  /**
   * Updates an existing database entry, with the passed/failed information for
   * a particular buildbot. If no entry is associated to the commit hash provided,
   * creates a new entry from the metadata included in the {@code updateData}.
   *
   * @param updateData a JSON in plain string format, must have a "commitHash" field.
   * @throws IllegalArgumentException if entryData is null or doesn't have a
   *     "commitHash" field.
   */
  void updateExistingEntry(final String updateData);

  /**
   * Deletes all entries from the database that have a certain property equal to
   * a provided value. If no such entry exists, ignores the request.
   *
   * @param property specifies the name of the property to search for.
   * @param value specifies the intended value of the property searched by.
   */
  void deleteEntries(final String property, final String value);

  /**
   * Queries the database for all the entries which have a certain property
   * equal to a provided value.
   *
   * @param property specifies the name of the property to search for.
   * @param value specifies the intended value of the property searched by.
   * @return an iterable object containing all the database entries that satisfy
   *     the condition.
   * @throws IllegalArgumentException if either property or value are null.
   */
  Iterable<String> getEntriesByProperty(final String property, final String value);

  /**
   * Queries the database for a specified amout of entries, going down in chronological
   * order, starting from an offest.
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
  Iterable<String> getLastEntries(final int number, final int offset)
                                                    throws IllegalArgumentException;
}
