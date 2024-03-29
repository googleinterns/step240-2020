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

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Test;

public class DatastoreRepositoryTests {
  private LocalDatastoreHelper emulator =
      LocalDatastoreHelper.newBuilder().setConsistency(1.0).setStoreOnDisk(false).build();

  public DatastoreRepositoryTests() throws IOException, InterruptedException {
    emulator.start();
  }

  private BuildInfo getDummyEntity(String commitHash, Timestamp time) {
    return new BuildInfo(getDummyGitData(commitHash, time));
  }

  private GitHubData getDummyGitData(String commitHash, Timestamp time) {
    return new GitHubData(commitHash, time);
  }

  private BuildBotData getDummyUpdate(String commitHash) {
    return new BuildBotData(commitHash, "tester", new ArrayList<>(), BuilderStatus.PASSED);
  }

  @After
  public void reset() throws IOException {
    emulator.reset();
  }

  @Test
  public void testValidAddition() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertEquals(getDummyEntity("1", time), (storage.getRevisionEntry("1")));
  }

  @Test
  public void testValidUpdate() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));

    BuildInfo dummy = getDummyEntity("1", time);
    dummy.addBuilder(getDummyUpdate("1"));
    assertEquals(dummy, storage.getRevisionEntry("1"));
  }

  @Test
  public void testValidDeletion() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertTrue(storage.deleteRevisionEntry("1"));
    assertNull(storage.getRevisionEntry("1"));
  }

  /** Tests the behaviour of add/update/get/delete requests on null data. */
  @Test
  public void testRequestsNullData() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertThrows(
        NullPointerException.class,
        () -> {
          storage.createRevisionEntry(null);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          storage.getRevisionEntry(null);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          storage.updateRevisionEntry(null);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          storage.deleteRevisionEntry(null);
        });
  }

  @Test
  public void testAddingSameEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());
    Timestamp time = Timestamp.ofTimeMicroseconds(0);

    assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    assertFalse(storage.createRevisionEntry(getDummyGitData("1", time)));
  }

  @Test
  public void testUpdatingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertFalse(storage.updateRevisionEntry(getDummyUpdate("1")));
  }

  @Test
  public void testGettingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertNull(storage.getRevisionEntry("1"));
  }

  @Test
  public void testDeletingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertTrue(storage.deleteRevisionEntry("1"));
  }

  /** Test possible cases of the getLastRevisions query. */
  @Test
  public void testGetLastRevisionsRegularQuery() throws IOException, InterruptedException {
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

  @Test
  public void testGetLastRevisionsQueryOffset() throws IOException, InterruptedException {
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

  @Test
  public void testGetLastRevisionsQueryAfterDeletion() throws IOException, InterruptedException {
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
}
