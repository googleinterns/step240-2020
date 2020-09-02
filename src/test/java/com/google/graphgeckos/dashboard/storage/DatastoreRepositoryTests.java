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
  public void testRequestSequenceValidData() {
    DatastoreRepository storage = new DatastoreRepository();
    
    // Test valid addition
    Assert.assertTrue(storage.createRevisionEntry(getDummyEntity("1")));
    Assert.assertEquals(getDummyEntity("1"), storage.getRevisionEntry("1"));

    // Test valid update
    Assert.assertTrue(storage.updateRevisionEntry(getDummyUpdate("1")));
    BuildInfo dummy = getDummyEntity("1");
    dummy.addBuilder(new Builder(getDummyUpdate("1")));
    Assert.assertEquals(dummy, storage.getRevisionEntry("1"));

    // Test valid deletion
    Assert.assertTrue(storage.deleteRevisionEntry("1"));
    Assert.assertEquals(null, storage.getRevisionEntry("1"));
  }

  @Test
  public void testRequestsNullData() {
    DatastoreRepository storage = new DatastoreRepository();

    Assert.assertFalse(storage.createRevisionEntry(null));
    Assert.assertFalse(storage.getRevisionEntry(null));
    Assert.assertFalse(storage.updateRevisionEntry(null));
    Assert.assertFalse(storage.deleteRevisionEntry(null));
  }

  @Test
  public void testRequestSequenceInvalidData() {
    DatastoreRepository storage = new DatastoreRepository();

    // Test adding same entry
    Assert.assertTrue(storage.createRevisionEntry(getDummyEntity("1")));
    Assert.assertFalse(storage.createRevisionEntry(getDummyEntity("1")));

    // Test updating inexistent entry
    Assert.assertFalse(storage.updateRevisionEntry(getDummyUpdate("2")));

    // Test getting inexistent entry
    Assert.assertEquals(null, storage.getRevisionEntry("2"));

    // Test deleting inexistent entry
    Assert.assertTrue(storage.deleteRevisionEntry("2"));
  }

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

  @Test
  public void testGetLastRevisions() {

  }
}
