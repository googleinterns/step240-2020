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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatastoreRepositoryTests {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private BuildInfo getDummyEntity(String commitHash) {
    return new BuildInfo(new ParsedGitData(commitHash, Timestamp.now(), "test"));
  }

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /**
   * Tests proper deleting/preserving a single entity stored in the datastore.
   */
  @Test
  private void testSingleEntityCleanup() {
    DatastoreRepository storage = new DatastoreRepository();

    // Test deletion when entry is older than the timestamp
    BuildInfo dummy = getDummyEntity("1");
    storage.createRevisionEntry(dummy);
    storage.deleteEntriesOlderThan(Timestamp.now());
    Assert.assertTrue(storage.getRevisionEntry("1") == null);

    // Test preservation when the entry is created at the same time as the timestamp
    dummy = getDummyEntity("1");
    storage.createRevisionEntry(dummy);
    Timestamp savedTime = dummy.getTimestamp();
    storage.deleteEntriesOlderThan(savedTime);
    Assert.assertFalse(storage.getRevisionEntry("1") == null);
    storage.deleteRevisionEntry("1");

    // Test preservation when the entry is newer than the timestamp
    dummy = getDummyEntity("1");
    storage.createRevisionEntry(dummy);
    storage.deleteEntriesOlderThan(savedTime);
    Assert.assertFalse(storage.getRevisionEntry("1") == null);
  }

  /**
   * Tests proper deleting/preserving three entities stored in the datastore.
   */
  @Test
  private void testMultipleEntityCleanup() {
    DatastoreRepository storage = new DatastoreRepository();
    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");

    // Test multiple deletions for all entries older than the timestamp
    storage.createRevisionEntry(dummy1);
    storage.createRevisionEntry(dummy2);
    storage.createRevisionEntry(dummy3);

    storage.deleteEntriesOlderThan(Timestamp.now());
    Assert.assertTrue(storage.getRevisionEntry("1") == null);
    Assert.assertTrue(storage.getRevisionEntry("2") == null);
    Assert.assertTrue(storage.getRevisionEntry("3") == null);

    // Test partial deletion of entries
    dummy3 = getDummyEntity("3");

    storage.createRevisionEntry(dummy1);
    storage.createRevisionEntry(dummy2);
    storage.createRevisionEntry(dummy3);
    Timestamp savedTime = dummy3.getTimestamp();

    storage.deleteEntriesOlderThan(savedTime);
    Assert.assertTrue(storage.getRevisionEntry("1") == null);
    Assert.assertTrue(storage.getRevisionEntry("2") == null);
    Assert.assertFalse(storage.getRevisionEntry("3") == null);
    storage.deleteRevisionEntry("3");

    // Test no deletion of entries
    dummy1 = getDummyEntity("1");
    dummy2 = getDummyEntity("2");
    dummy3 = getDummyEntity("3");

    storage.createRevisionEntry(dummy1);
    storage.createRevisionEntry(dummy2);
    storage.createRevisionEntry(dummy3);

    storage.deleteEntriesOlderThan(savedtime);
    Assert.assertFalse(storage.getRevisionEntry("1") == null);
    Assert.assertFalse(storage.getRevisionEntry("2") == null);
    Assert.assertFalse(storage.getRevisionEntry("3") == null);
  }
}
