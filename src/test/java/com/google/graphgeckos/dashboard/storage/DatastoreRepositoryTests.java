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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.cloud.Timestamp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatastoreRepositoryTests {
  private LocalDatastoreHelper emulator;
  private Datastore localDatastore;

  private BuildInfo getDummyEntity(String commitHash, Timestamp time) {
    return new BuildInfo(getDummyGitData(commitHash, time));
  }

  private ParsedGitData getDummyGitData(String commitHash, Timestamp time) {
    return new ParsedGitData(commitHash, time, "test");
  }

  private ParsedBuildbotData getDummyUpdate(String commitHash) {
    return new ParsedBuildbotData(commitHash, "tester", new ArrayList<>(), BuilderStatus.PASSED);
  }

  @Before
  public void setUp() throws IOException, InterruptedException {
    emulator = LocalDatastoreHelper.newBuilder().setConsistency(1.0)
                                                .setStoreOnDisk(false)
                                                .build();
    emulator.start();
    localDatastore = emulator.getOptions().getService();
  }

  @Test
  public void testValidAddition() throws IOException, InterruptedException{
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time = Timestamp.ofTimeMicroseconds(1);
    
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    Assert.assertEquals(getDummyEntity("1", time), (storage.getRevisionEntry("1")));
  }

  @Test
  public void testValidUpdate() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time = Timestamp.ofTimeMicroseconds(1);

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    Assert.assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));
    BuildInfo dummy = getDummyEntity("1", time);
    dummy.addBuilder(new Builder(getDummyUpdate("1")));
    Assert.assertEquals(dummy, storage.getRevisionEntry("1"));
  }

  @Test
  public void testValidDeletion() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time = Timestamp.ofTimeMicroseconds(1);

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    Assert.assertTrue(storage.deleteRevisionEntry("1"));
    Assert.assertNull(storage.getRevisionEntry("1"));
  }

  /**
   * Tests the behaviour of add/update/get/delete requests on null data.
   */
  @Test
  public void testRequestsNullData() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);

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
  }

  @Test
  public void testAddingSameEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time = Timestamp.ofTimeMicroseconds(1);

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time)));
    Assert.assertFalse(storage.createRevisionEntry(getDummyGitData("1", time)));
  }

  @Test
  public void testUpdatingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);

    Assert.assertFalse(storage.updateRevisionEntry(getDummyUpdate("1")));
  }

  @Test
  public void testGettingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);

    Assert.assertNull(storage.getRevisionEntry("1"));
  }

  @Test
  public void testDeletingInexistentEntry() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);

    Assert.assertTrue(storage.deleteRevisionEntry("1"));
  }

  /**
   * Test possible cases of the getLastRevisions query.
   */
  @Test
  public void testGetLastRevisionsRegularQuery() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time1 = Timestamp.ofTimeMicroseconds(1);
    Timestamp time2 = Timestamp.ofTimeMicroseconds(2);
    Timestamp time3 = Timestamp.ofTimeMicroseconds(3);
    Timestamp time4 = Timestamp.ofTimeMicroseconds(4);
    Timestamp time5 = Timestamp.ofTimeMicroseconds(5);

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time1)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("2", time2)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("3", time3)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("4", time4)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("5", time5)));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 0);
    Assert.assertEquals(results.size(), 3);
    Assert.assertEquals(results.get(0), getDummyEntity("5", time5));
    Assert.assertEquals(results.get(1), getDummyEntity("4", time4));
    Assert.assertEquals(results.get(2), getDummyEntity("3", time3));
  }

  @Test
  public void testGetLastRevisionsQueryOffset() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time1 = Timestamp.ofTimeMicroseconds(1);
    Timestamp time2 = Timestamp.ofTimeMicroseconds(2);
    Timestamp time3 = Timestamp.ofTimeMicroseconds(3);
    Timestamp time4 = Timestamp.ofTimeMicroseconds(4);
    Timestamp time5 = Timestamp.ofTimeMicroseconds(5);

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time1)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("2", time2)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("3", time3)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("4", time4)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("5", time5)));

    List<BuildInfo> results = storage.getLastRevisionEntries(3, 2);
    Assert.assertEquals(results.size(), 3);
    Assert.assertEquals(results.get(0), getDummyEntity("3", time3));
    Assert.assertEquals(results.get(1), getDummyEntity("2", time2));
    Assert.assertEquals(results.get(2), getDummyEntity("1", time1));
  }

  @Test
  public void testGetLastRevisionsQueryAfterDeletion() throws IOException, InterruptedException {
    DatastoreRepository storage = new DatastoreRepository(localDatastore);
    Timestamp time1 = Timestamp.ofTimeMicroseconds(1);
    Timestamp time2 = Timestamp.ofTimeMicroseconds(2);
    Timestamp time3 = Timestamp.ofTimeMicroseconds(3);
    Timestamp time4 = Timestamp.ofTimeMicroseconds(4);
    Timestamp time5 = Timestamp.ofTimeMicroseconds(5);

    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("1", time1)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("2", time2)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("3", time3)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("4", time4)));
    Assert.assertTrue(storage.createRevisionEntry(getDummyGitData("5", time5)));

    Assert.assertTrue(storage.deleteRevisionEntry("3"));
  
    List<BuildInfo> results = storage.getLastRevisionEntries(3, 2);
    Assert.assertEquals(results.size(), 2);
    Assert.assertEquals(results.get(0), getDummyEntity("2", time2));
    Assert.assertEquals(results.get(1), getDummyEntity("1", time1));
  }
}
