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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatastoreRepositoryTests {
  private LocalDatastoreHelper emulator = LocalDatastoreHelper.newBuilder().setConsistency(1.0)
                                                                           .setStoreOnDisk(false)
                                                                           .build();
  /**
   * Starts the GC Datastore emulator.
   */
  public DatastoreRepositoryTests() throws IOException, InterruptedException {
    emulator.start();
  }

  private BuildInfo getDummyEntity(String commitHash, Timestamp time) {
    return new BuildInfo(getDummyGitData(commitHash, time));
  }

  private GitHubData getDummyGitData(String commitHash, Timestamp time) {
    return new GitHubData(commitHash, time, "test");
  }

  private BuildBotData getDummyUpdate(String commitHash) {
    return new BuildBotData(commitHash, "tester", new ArrayList<>(), BuilderStatus.PASSED);
  }

  /**
   * Resets the Datastore emulator to a blank state after each test.
   */
  @After
  public void reset() throws IOException {
    emulator.reset();
  }

  /**
   * Tests if DatastoreRepository can create a new "revision" entry and then get it back intact.
   * The creation should succeed, and the value retrieved should be equal to the one that was set.
   */
  @Test
  public void testValidAddition() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);
    
    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertEquals(getDummyEntity("1", time), (storage.getRevisionEntry("1")));
  }

  /**
   * Tests if DatastoreRepository can create a new entry, update and get the updated entry back.
   * The creation and update should succeed, and the value retrieved should be equal to the
   * creation data + the update.
   */
  @Test
  public void testValidUpdate() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));

    BuildInfo dummy = getDummyEntity("1", time);
    dummy.addBuilder(getDummyUpdate("1"));
    assertEquals(dummy, storage.getRevisionEntry("1"));
  }

  /**
   * Tests that after deleting an entry there is no leftover entity with the same key.
   * Both creation and deletion should succeed, and the get request should return null.
   */
  @Test
  public void testValidDeletion() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertTrue(storage.deleteRevisionEntry("1"));
    assertNull(storage.getRevisionEntry("1"));
  }

  /**
   * Tests the behavior of all DatastoreRepository methods on null data. All should return NPE.
   */
  @Test
  public void testRequestsNullData() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertThrows(IllegalArgumentException.class, () -> {
      storage.createRevisionEntry(null);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      storage.getRevisionEntry(null);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      storage.updateRevisionEntry(null);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      storage.deleteRevisionEntry(null);
    });

    assertThrows(NullPointerException.class, () -> {
      storage.getBuildbotIndex(null);
    });

    assertThrows(NullPointerException.class, () -> {
      storage.setBuildbotIndex(null, 0);
    });

    assertThrows(NullPointerException.class, () -> {
      storage.registerNewBuildbot(null, 0);
    });
  }

  /**
   * Tests the behavior of DatastoreRepository when creating two entries with the same key.
   * The first request should succeed, and the second one should fail.
   */
  @Test
  public void testAddingSameEntry() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertFalse(storage.createRevisionEntry(getDummyGitData("1", time)));
  }

  /**
   * Tests the behavior of DatastoreRepository when an update as attempted on an inexistent key.
   * The request should fail.
   */
  @Test
  public void testUpdatingInexistentEntry() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertFalse(storage.updateRevisionEntry(getDummyUpdate("1")));
  }

  /**
   * Tests the behavior of DatastoreRepository when a get is attempted on an inexistent key.
   * The request should return null.
   */
  @Test
  public void testGettingInexistentEntry() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertNull(storage.getRevisionEntry("1"));
  }

  /**
   * Tests the behavior of DatastoreRepository when a deletion is attempted on an inexistent key.
   * The request should succeed.
   */
  @Test
  public void testDeletingInexistentEntry() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertTrue(storage.deleteRevisionEntry("1"));
  }

  /**
   * Test the behavior of a getLastRevisionEntries query with no offset and enough data.
   * The request should return the latest 3 entries, in a descending order.
   */
  @Test
  public void testGetLastRevisionsRegularQuery() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time1 = Timestamp.ofTimeMicroseconds(1);
    Timestamp time2 = Timestamp.ofTimeMicroseconds(2);
    Timestamp time3 = Timestamp.ofTimeMicroseconds(3);
    Timestamp time4 = Timestamp.ofTimeMicroseconds(4);
    Timestamp time5 = Timestamp.ofTimeMicroseconds(5);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time1)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("2", time2)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("3", time3)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("4", time4)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("5", time5)));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 0);
    assertEquals(results.size(), 3);
    assertEquals(results.get(0), getDummyEntity("5", time5));
    assertEquals(results.get(1), getDummyEntity("4", time4));
    assertEquals(results.get(2), getDummyEntity("3", time3));
  }

  /**
   * Test the behavior of a getLastRevisionEntries query with an offset and enough data.
   * The request should return the latest entries starting from the 3rd latest,
   * in descending order.
   */
  @Test
  public void testGetLastRevisionsQueryOffset() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time1 = Timestamp.ofTimeMicroseconds(1);
    Timestamp time2 = Timestamp.ofTimeMicroseconds(2);
    Timestamp time3 = Timestamp.ofTimeMicroseconds(3);
    Timestamp time4 = Timestamp.ofTimeMicroseconds(4);
    Timestamp time5 = Timestamp.ofTimeMicroseconds(5);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time1)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("2", time2)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("3", time3)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("4", time4)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("5", time5)));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 2);
    assertEquals(results.size(), 3);
    assertEquals(results.get(0), getDummyEntity("3", time3));
    assertEquals(results.get(1), getDummyEntity("2", time2));
    assertEquals(results.get(2), getDummyEntity("1", time1));
  }

  /**
   * Test the behavior of a getLastRevisionEntries query with an offset and insufficient data.
   * The request should return only 2 out of 3 requested entries, since entry #3 was deleted,
   * and the first two were skipped.
   */
  @Test
  public void testGetLastRevisionsQueryAfterDeletion() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time1 = Timestamp.ofTimeMicroseconds(1);
    Timestamp time2 = Timestamp.ofTimeMicroseconds(2);
    Timestamp time3 = Timestamp.ofTimeMicroseconds(3);
    Timestamp time4 = Timestamp.ofTimeMicroseconds(4);
    Timestamp time5 = Timestamp.ofTimeMicroseconds(5);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time1)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("2", time2)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("3", time3)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("4", time4)));
    assertTrue(storage.createRevisionEntry(getDummyGitData("5", time5)));

    assertTrue(storage.deleteRevisionEntry("3"));
  
    List<BuildInfo> results = storage.getLastRevisionEntries(3, 2);

    assertEquals(results.size(), 2);
    assertEquals(results.get(0), getDummyEntity("2", time2));
    assertEquals(results.get(1), getDummyEntity("1", time1));
  }

  /**
   * Tests if DatastoreRepository can create a new "index" entry and then get it back intact.
   * The value retrieved should be the one that was set.
   */
  @Test
  public void testValidGetAndSetIndexRequests() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.setBuildbotIndex("tester", 1000);
    
    assertEquals(1000, storage.getBuildbotIndex("tester"));
  }

  /**
   * Tests the behavior when a getBuildbotIndex is attempted for an unregistered buildbot.
   * The request should throw {@link #BuildbotNotFoundException BuildbotNotFoundException}.
   */
  @Test
  public void testInvalidGetIndexRequest() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertThrows(BuildbotNotFoundException.class, () -> {
      storage.getBuildbotIndex("tester");
    });
  }

  /**
   * Tests the behavior when an index is updated with a lesser and equal value than
   * the current one. Both updates should fail and throw an exception.
   */
  @Test
  public void testInvalidSetIndexRequest() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.setBuildbotIndex("tester", 1000);

    assertThrows(IllegalArgumentException.class, () -> {
      storage.setBuildbotIndex("tester", 1);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      storage.setBuildbotIndex("tester", 1000);
    });
  }

  /**
   * Tests if registerNewBuildbot creates a proper "index" entry that can be retrieved.
   * The value retrieved should be equal to the one registered.
   */
  @Test
  public void testValidRegistration() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.registerNewBuildbot("test", 1);

    assertEquals(1, storage.getBuildbotIndex("test"));
  }

  /**
   * Tests if the same buildbot can be registered twice, the second time with a bigger index.
   * The second registration should overwrite the previous one.
   */
  @Test
  public void testAlreadyRegisteredLargerIndex() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.registerNewBuildbot("test", 1);

    storage.registerNewBuildbot("test", 3);

    assertEquals(3, storage.getBuildbotIndex("test"));
  }

  /**
   * Tests the behavior when the same buildbot is registered twice, with the same value.
   * No exceptions should be thrown, and both registrations should succeed.
   */
  @Test
  public void testAlreadyRegisteredSameIndex() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.registerNewBuildbot("test", 1);

    storage.registerNewBuildbot("test", 1);

    assertEquals(1, storage.getBuildbotIndex("test"));
  }

  /**
   * Tests the behaviour when the same buildbot is registered twice, and the second value is
   * smaller than the first one. The second registration should fail and throw an exception.
   */
  @Test
  public void testAlreadyRegisteredSmallerIndex() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.registerNewBuildbot("test", 3);

    assertThrows(IllegalArgumentException.class, () -> {
      storage.registerNewBuildbot("test", 1);
    });
  }

  /**
   * Tests the behaviour when a buildbot is registered for the first time with a negative index.
   * The registration should fail and throw an exception.
   */
    @Test
  public void testFirstRegistrationNegative() {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertThrows(IllegalArgumentException.class, () -> {
      storage.registerNewBuildbot("test", -1);
    });
  }
}
