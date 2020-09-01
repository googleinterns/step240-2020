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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatastoreRepositoryTests {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final long initialCleanupDelay = 3;
  private final long cleanupPeriod = 4;
  private final TimeUnit cleanupPeriodTimeunit = TimeUnit.MILLISECONDS;
  private final int ttl = 2;
  private final int ttlTimeUnit = Calendar.MILLISECOND;

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
   *
   * Timeline:
   * add : 0s
   * ttl expires : 2s
   * cleanup w/ delete : 3s
   * check : 6s
   * add : 6s
   * cleanup w/out delete: 7s
   * ttl expires: 8s
   * check: 9s
   * cleanup w/ delete: 11s
   */
  @Test
  private void testSingleEntityCleanup() {
    DatastoreRepository storage = new DatastoreRepository(initialCleanupDelay, cleanupPeriod,
                                                          cleanupPeriodTimeunit, ttl, ttlTimeUnit);
    storage.createRevisionEntry(getDummyEntity("1"));

    Thread.sleep(6);
    Assert.assertTrue(storage.getRevisionEntry("1") == null);
    
    storage.createRevisionEntry(getDummyEntity("1"));

    Thread.sleep(3);
    Assert.assertFalse(storage.getRevisionEntry("1") == null);

    Thread.sleep(2);
    Assert.assertTrue(storage.getRevisionEntry("1") == null);
  }

  /**
   * Tests proper deleting/preserving three entities stored in the datastore.
   *
   * Timeline:
   * add all 3 : 0s
   * ttl expires : 2s
   * cleanup w/ delete : 3s
   * check : 4s
   * add 1 & 2 : 4s
   * add 3 : 6s
   * ttl expires 1 & 2 : 6s
   * cleanup w/ partial delete : 7s
   * ttl expires 3 : 8s
   * check : 9s
   * manual delete 3 : 9s
   * add all 3 : 10s
   * cleanup w/out delete: 11s
   * ttl expires all 3 : 12s
   * check : 12s
   */
  @Test
  private void testMultipleEntityCleanup() {
    DatastoreRepository storage = new DatastoreRepository(initialCleanupDelay, cleanupPeriod,
                                                          cleanupPeriodTimeunit, ttl, ttlTimeUnit);
    storage.createRevisionEntry(getDummyEntity("1"));
    storage.createRevisionEntry(getDummyEntity("2"));
    storage.createRevisionEntry(getDummyEntity("3"));

    Thread.sleep(4);
    Assert.assertTrue(storage.getRevisionEntry("1") == null);
    Assert.assertTrue(storage.getRevisionEntry("2") == null);
    Assert.assertTrue(storage.getRevisionEntry("3") == null);

    storage.createRevisionEntry(getDummyEntity("1"));
    storage.createRevisionEntry(getDummyEntity("2"));

    Thread.sleep(2);

    storage.createRevisionEntry(getDummyEntity("3"));

    Thread.sleep(3);
    Assert.assertTrue(storage.getRevisionEntry("1") == null);
    Assert.assertTrue(storage.getRevisionEntry("2") == null);
    Assert.assertFalse(storage.getRevisionEntry("3") == null);

    storage.delete("3");

    Thread.sleep(1);

    storage.createRevisionEntry(getDummyEntity("1"));
    storage.createRevisionEntry(getDummyEntity("2"));
    storage.createRevisionEntry(getDummyEntity("3"));

    Thread.sleep(2);
    Assert.assertFalse(storage.getRevisionEntry("1") == null);
    Assert.assertFalse(storage.getRevisionEntry("2") == null);
    Assert.assertFalse(storage.getRevisionEntry("3") == null);
  }
}
