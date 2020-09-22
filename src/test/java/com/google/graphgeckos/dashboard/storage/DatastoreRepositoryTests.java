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

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatastoreRepositoryTests {
  LocalDatastoreHelper emulator = LocalDatastoreHelper.newBuilder().setConsistency(1.0)
                                                                   .setStoreOnDisk(false)
                                                                   .build();

  private BuildInfo getDummyEntity(String commitHash) {
    return new BuildInfo(getDummyGitData(commitHash));
  }

  private GitHubData getDummyGitData(String commitHash) {
    return new GitHubData(commitHash, Timestamp.now(), "test");
  }

  private BuildBotData getDummyUpdate(String commitHash) {
    return new BuildBotData(commitHash, "tester", new ArrayList<>(), BuilderStatus.PASSED);
  }

  private Entity getIndexEntity(String name, int index) {
    return Entity.Builder.newBuilder(name).set("index")
  }

  @Before
  public void setUp() throws IOException, InterruptedException {
    emulator.start();
  }

  @After
  public void tearDown() throws IOException, InterruptedException, TimeoutException  {
    emulator.stop();
  }

  @Test
  public void testValidAddition() throws IOException, InterruptedException{
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertEquals(getDummyEntity("1"), storage.getRevisionEntry("1"));
  }

  @Test
  public void testValidUpdate() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));
    BuildInfo dummy = getDummyEntity("1");
    dummy.addBuilder(new BuildBotInfo(getDummyUpdate("1")));
    Assert.assertEquals(dummy, storage.getRevisionEntry("1"));
  }

  @Test
  public void testValidDeletion() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertTrue(storage.deleteRevisionEntry("1"));
    Assert.assertNull(storage.getRevisionEntry("1"));
  }

  /**
   * Tests the behaviour of add/update/get/delete requests on null data.
   */
  @Test
  public void testRequestsNullData() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      storage.createRevisionEntry(null);
    });

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      storage.getRevisionEntry(null);
    });

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      storage.updateRevisionEntry(null);
    });

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      storage.deleteRevisionEntry(null);
    });

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      storage.getBuildbotIndex(null);
    });

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      storage.setBuildbotIndex(null, 0);
    });
  }

  @Test
  public void testAddingSameEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertFalse(storage.createRevisionEntry(getDummyGitData("1")));
  }

  @Test
  public void testUpdatingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertFalse(storage.updateRevisionEntry(getDummyUpdate("2")));
  }

  @Test
  public void testGettingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertNull(storage.getRevisionEntry("2"));
  }

  @Test
  public void testDeletingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.deleteRevisionEntry("2"));
  }

  /**
   * Test possible cases of the getLastRevisions query.
   */
  @Test
  public void testGetLastRevisionsRegularQuery() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");
    BuildInfo dummy4 = getDummyEntity("4");
    BuildInfo dummy5 = getDummyEntity("5");

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("2")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("3")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("4")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("5")));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 0);
    Assert.assertEquals(results.size(), 3);
    Assert.assertEquals(results.get(0), dummy5);
    Assert.assertEquals(results.get(1), dummy4);
    Assert.assertEquals(results.get(2), dummy3);
  }

  @Test
  public void testGetLastRevisionsQueryOffset() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");
    BuildInfo dummy4 = getDummyEntity("4");
    BuildInfo dummy5 = getDummyEntity("5");

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("2")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("3")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("4")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("5")));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 2);
    Assert.assertEquals(results.size(), 3);
    Assert.assertEquals(results.get(0), dummy3);
    Assert.assertEquals(results.get(1), dummy2);
    Assert.assertEquals(results.get(2), dummy1);
  }

  @Test
  public void testGetLastRevisionsQueryAfterDeletion() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");
    BuildInfo dummy4 = getDummyEntity("4");
    BuildInfo dummy5 = getDummyEntity("5");

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("2")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("3")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("4")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("5")));

    Assert.assertTrue(storage.deleteRevisionEntry("3"));

    List<BuildInfo> results = storage.getLastRevisionEntries(2, 2);
    Assert.assertEquals(results.size(), 2);
    Assert.assertEquals(results.get(0), dummy2);
    Assert.assertEquals(results.get(1), dummy1);
  }

  @Test
  public void testValidGetAndSetIndexRequests() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.setBuildbotIndex("tester", 1000);
    
    assertEquals(1000, storage.getBuildbotIndex("tester"));
  }

  @Test
  public void testInvalidGetIndexRequest() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    assertThrows(BuildbotNotFoundException.class, () -> {
      storage.getBuildbotIndex("tester");
    });
  }

  @Test
  public void testInvalidSetIndexRequest() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(emulator.getOptions().getService());

    storage.setBuildbotIndex("tester", 1000);

    assertThrows(IndexOutOfBoundsException.class, () -> {
      storage.setBuildbotIndex("tester", 1);
    });

    assertThrows(IndexOutOfBoundsException.class, () -> {
      storage.setBuildbotIndex("tester", 1000);
    });
  }
}
