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
   * Test deletion when entry is older than the timestamp.
   */
  @Test
  public void testSingleEntityOlderCleanup() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy = getDummyEntity("1");
    storage.createRevisionEntry(dummy);
    storage.deleteEntriesOlderThan(Timestamp.now());

    Assert.assertNull(storage.getRevisionEntry("1"));
  }

  /**
   * Test preservation when the entry is created at the same time as the timestamp.
   */
  @Test
  public void testSingleEntitySameAgeCleanup() {
    DatastoreRepository storage = new DatastoreRepository();

    dummy = getDummyEntity("1");
    storage.createRevisionEntry(dummy);
    Timestamp savedTime = dummy.getTimestamp();
    storage.deleteEntriesOlderThan(savedTime);

    Assert.assertNotNull(storage.getRevisionEntry("1"));
  }

  /**
   * Test preservation when the entry is newer than the timestamp.
   */
  @Test
  public void testSingleEntityNewerCleanup() {
    DatastoreRepository storage = new DatastoreRepository();

    dummy = getDummyEntity("1");
    storage.createRevisionEntry(dummy);
    storage.deleteEntriesOlderThan(savedTime);

    Assert.assertNotNull(storage.getRevisionEntry("1"));
  }

  /**
   * Test multiple deletions for all entries older than the timestamp.
   */
  @Test
  private void testMultipleEntityCleanupAllDeleted() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");

    storage.createRevisionEntry(dummy1);
    storage.createRevisionEntry(dummy2);
    storage.createRevisionEntry(dummy3);

    storage.deleteEntriesOlderThan(Timestamp.now());

    Assert.assertNull(storage.getRevisionEntry("1"));
    Assert.assertNull(storage.getRevisionEntry("2"));
    Assert.assertNull(storage.getRevisionEntry("3"));
  }

  /**
   * Test partial deletion of entries.
   */
  @Test
  private void testMultipleEntityCleanupPartialDeleted() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");

    storage.createRevisionEntry(dummy1);
    storage.createRevisionEntry(dummy2);
    storage.createRevisionEntry(dummy3);
    Timestamp savedTime = dummy3.getTimestamp();

    storage.deleteEntriesOlderThan(savedTime);

    Assert.assertNull(storage.getRevisionEntry("1"));
    Assert.assertNull(storage.getRevisionEntry("2"));
    Assert.assertNotNull(storage.getRevisionEntry("3"));
  }

  /**
   * Test no deletion of entries.
   */
  @Test
  private void testMultipleEntityCleanupNoDeletion() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");

    storage.createRevisionEntry(dummy1);
    storage.createRevisionEntry(dummy2);
    storage.createRevisionEntry(dummy3);

    storage.deleteEntriesOlderThan(savedtime);

    Assert.assertNotNull(storage.getRevisionEntry("1"));
    Assert.assertNotNull(storage.getRevisionEntry("2"));
    Assert.assertNotNull(storage.getRevisionEntry("3"));
  }
}
