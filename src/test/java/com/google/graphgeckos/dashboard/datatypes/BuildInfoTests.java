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

package com.google.graphgeckos.dashboard.datatypes;

import static org.junit.Assert.assertEquals;

import com.google.cloud.Timestamp;
import java.util.ArrayList;
import org.junit.Test;

public class BuildInfoTests {
  private BuildInfo getDummyEntity() {
    return new BuildInfo(new GitHubData("dummy", Timestamp.ofTimeMicroseconds(0), "test"));
  }

  private BuildBotData getDummyUpdate(BuilderStatus status) {
    return new BuildBotData("dummy", "tester", new ArrayList<>(), status);
  }

  @Test
  public void testDefaultStatus() {
    BuildInfo dummy = getDummyEntity();

    assertEquals(RevisionStatus.lost, dummy.getStatus());
  }

  @Test
  public void testAllPassedStatus() {
    BuildInfo dummy = getDummyEntity();

    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));

    dummy.reanalyseStatus();

    assertEquals(RevisionStatus.passed, dummy.getStatus());
  }

  @Test
  public void testPassedAndLost() {
    BuildInfo dummy = getDummyEntity();

    dummy.addBuilder(getDummyUpdate(BuilderStatus.LOST));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.LOST));

    dummy.reanalyseStatus();

    assertEquals(RevisionStatus.passed, dummy.getStatus());
  }

  @Test
  public void testAllLost() {
    BuildInfo dummy = getDummyEntity();

    dummy.addBuilder(getDummyUpdate(BuilderStatus.LOST));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.LOST));

    dummy.reanalyseStatus();

    assertEquals(RevisionStatus.lost, dummy.getStatus());
  }

  @Test
  public void testPassedAndFailed() {
    BuildInfo dummy = getDummyEntity();

    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.FAILED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));

    dummy.reanalyseStatus();

    assertEquals(RevisionStatus.failed, dummy.getStatus());
  }

  @Test
  public void testAllFailed() {
    BuildInfo dummy = getDummyEntity();

    dummy.addBuilder(getDummyUpdate(BuilderStatus.FAILED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.FAILED));

    dummy.reanalyseStatus();

    assertEquals(RevisionStatus.failed, dummy.getStatus());
  }

  @Test
  public void testMixed() {
    BuildInfo dummy = getDummyEntity();

    dummy.addBuilder(getDummyUpdate(BuilderStatus.FAILED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.PASSED));
    dummy.addBuilder(getDummyUpdate(BuilderStatus.LOST));

    dummy.reanalyseStatus();

    assertEquals(RevisionStatus.failed, dummy.getStatus());
  }
}
