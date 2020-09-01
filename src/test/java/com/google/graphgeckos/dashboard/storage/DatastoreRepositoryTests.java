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

  private final long initialCleanupDelayDefault = 1;
  private final long cleanupPeriodDefault = 1;
  private final TimeUnit cleanupPeriodTimeunitDefault = TimeUnit.WEEKS;
  private final int ttlDefault = 3;
  private final int ttlTimeUnit = Calendar.MONTH;

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

  @Test
  public void testValidRequestSequenceValidData() {
    DatastoreRepository storage = new DatastoreRepository(initialCleanupDelayDefault,
                                                          cleanupPeriodDefault,
                                                          cleanupPeriodTimeunitDefault,
                                                          ttlDefault,
                                                          ttlTimeUnitDefault);
    
    storage.add(getDummyEntity("1"));
    
  }

  @Test
  public void testValidRequestSequenceInvalidData() {

  }

  @Test
  public void testInvalidRequestSequenceValidData() {

  }

  @Test
  public void testParallelValidRequests() {

  }

  @Test
  public void testGetLastRevisions() {

  }
}
