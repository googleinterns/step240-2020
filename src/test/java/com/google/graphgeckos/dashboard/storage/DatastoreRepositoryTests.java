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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.Timestamp;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatastoreRepositoryTests {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private BuildInfo getDummyEntity(String commitHash) {
    return new BuildInfo(new ParsedGitData(commitHash, Timestamp.now(), "test"));
  }

  private ParsedBuildbotData getDummyUpdate(String commitHash) {
    return new ParsedBuildbotData(commitHash, "tester", new ArrayList<>(), BuilderStatus.PASSED);
  }

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testValidAddition() {
    DatastoreRepository storage = new DatastoreRepository();
    
    Assert.assertTrue(storage.createRevisionEntry(getDummyEntity("1")));
    Assert.assertEquals(getDummyEntity("1"), storage.getRevisionEntry("1"));
  }

  @Test
  public void testValidUpdate() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));
    BuildInfo dummy = getDummyEntity("1");
    dummy.addBuilder(new Builder(getDummyUpdate("1")));
    Assert.assertEquals(dummy, storage.getRevisionEntry("1"));
  }

  @Test
  public void testValidDeletion() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.deleteRevisionEntry("1"));
    Assert.assertNull(storage.getRevisionEntry("1"));
  }

  /**
   * Tests the behaviour of add/update/get/delete requests on null data.
   */
  @Test
  public void testRequestsNullData() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertFalse(storage.createRevisionEntry(null));
    Assert.assertFalse(storage.getRevisionEntry(null));
    Assert.assertFalse(storage.updateRevisionEntry(null));
    Assert.assertFalse(storage.deleteRevisionEntry(null));
  }

  @Test
  public void testAddingSameEntry() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.createRevisionEntry(getDummyEntity("1")));
    Assert.assertFalse(storage.createRevisionEntry(getDummyEntity("1")));
  }

  @Test
  public void testUpdatingInexistentEntry() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertFalse(storage.updateRevisionEntry(getDummyUpdate("2")));
  }

  @Test
  public void testGettingInexistentEntry() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertNull(storage.getRevisionEntry("2"));
  }

  @Test
  public void testDeletingInexistentEntry() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.deleteRevisionEntry("2"));
  }

  /**
   * Tests multiple threads creating update requests for the same entities.
   */
  @Test
  public void testParallelBurstRequests() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertTrue(storage.createRevisionEntry(getDummyEntity("1")));
    Assert.assertTrue(storage.createRevisionEntry(getDummyEntity("2")));

    Runnable burstRequest = () -> {
      for (int i = 0; i < 10; ++i) {
        Assert.assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));
        Assert.assertTrue(storage.updateRevisionEntry(getDummyUpdate("2")));
      }
    }

    Thread thread1 = new Thread(burstRequest);
    Thread thread2 = new Thread(burstRequest);
    Thread thread3 = new Thread(burstRequest);

    thread1.start();
    thread2.start();
    thread3.start();

    Assert.assertTrue(storage.deleteRevisionEntry("1"));
    Assert.assertTrue(storage.deleteRevisionEntry("2"));
  }

  /**
   * Test possible cases of the getLastRevisions query.
   */
  @Test
  public void testGetLastRevisionsRegularQuery() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");
    BuildInfo dummy4 = getDummyEntity("4");
    BuildInfo dummy5 = getDummyEntity("5");

    Assert.assertTrue(storage.createRevisionEntry(dummy1));
    Assert.assertTrue(storage.createRevisionEntry(dummy2));
    Assert.assertTrue(storage.createRevisionEntry(dummy3));
    Assert.assertTrue(storage.createRevisionEntry(dummy4));
    Assert.assertTrue(storage.createRevisionEntry(dummy5));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 0);
    AssertEquals(results.size(), 3);
    AssertEquals(results.get(0), dummy5);
    AssertEquals(results.get(1), dummy4);
    AssertEquals(results.get(2), dummy3);
  }

  @Test
  public void testGetLastRevisionsQueryOffset() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");
    BuildInfo dummy4 = getDummyEntity("4");
    BuildInfo dummy5 = getDummyEntity("5");

    Assert.assertTrue(storage.createRevisionEntry(dummy1));
    Assert.assertTrue(storage.createRevisionEntry(dummy2));
    Assert.assertTrue(storage.createRevisionEntry(dummy3));
    Assert.assertTrue(storage.createRevisionEntry(dummy4));
    Assert.assertTrue(storage.createRevisionEntry(dummy5));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 2);
    AssertEquals(results.size(), 3);
    AssertEquals(results.get(0), dummy3);
    AssertEquals(results.get(1), dummy2);
    AssertEquals(results.get(2), dummy1);
  }

  @Test
  public void testGetLastRevisionsQueryAfterDeletion() {
    DatastoreRepository storage = new DatastoreRepository();

    BuildInfo dummy1 = getDummyEntity("1");
    BuildInfo dummy2 = getDummyEntity("2");
    BuildInfo dummy3 = getDummyEntity("3");
    BuildInfo dummy4 = getDummyEntity("4");
    BuildInfo dummy5 = getDummyEntity("5");

    Assert.assertTrue(storage.createRevisionEntry(dummy1));
    Assert.assertTrue(storage.createRevisionEntry(dummy2));
    Assert.assertTrue(storage.createRevisionEntry(dummy3));
    Assert.assertTrue(storage.createRevisionEntry(dummy4));
    Assert.assertTrue(storage.createRevisionEntry(dummy5));

    Assert.assertTrue(storage.deleteRevisionEntry("3"));
  
    List<BuildInfo> results = storage.getLastRevisionEntries(2, 2);
    AssertEquals(results.size(), 2);
    AssertEquals(results.get(0), dummy2);
    AssertEquals(results.get(1), dummy1);
  }
}
