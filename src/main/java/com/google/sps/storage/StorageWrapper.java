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

import com.google.appengine.api.datastore.Entity;

/**
 * Implementation of the StorageWrapperInterface, with the purpose of providing I/O to
 * the Google Appengine Datastore component. Each individual database entry is
 * structured as follows:
 *    - Kind: "revision"
 *    - Properties:
 *      * commit hash
 *      * index
 *      * blames
 *      * timestamp
 *      * commit information
 */
public class StorageController implements StorageControllerInterface {
  void createNewEntry(final String entryData) throws IllegalArgumentException {

  }

  void updateExistingEntry(final String updateData) throws IllegalArgumentException {

  }

  Iterable<String> getEntriesByProperty(final String property, final String value)
                                                    throws IllegalArgumentException {
    return null;
  }

  Iterable<String> getLastEntries(final int number, final int offset)
                                                    throws IllegalArgumentException {
    return null;
  }
}
