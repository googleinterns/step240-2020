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

package com.google.graphgeckos.dashboard.buildbot;

import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.AbstractJsonTestInfo;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.Log;
import java.util.ArrayList;
import java.util.List;

public class BuildBotClientTestInfo extends AbstractJsonTestInfo {

  /**
   * Expected output fields. See {@link com.google.graphgeckos.dashboard.datatypes.BuildBotData} to learn more.
   */
  private String commitHash;
  private Timestamp timestamp;
  private String name;
  private BuilderStatus status;
  private List<Log> logs;

  public BuildBotClientTestInfo(String testName) {
    super(testName);
  }

  @Override
  protected String getPath() {
    return "src/test/resources/jsons/buildbots/";
  }

  /**
   * Values come in the following order: commitHash, timestamp, name, status, logs.
   * Each log is represented by the two-string sequence, where a[i] is the type of a log and a[i + 1]
   * is the link to a log. There can be no logs.
   */
  @Override
  protected void assignExpectedValues(String[] expected) {
    if (expected.length < 4 || expected.length % 2 == 1) {
      throw new IllegalArgumentException(
        String.format("Wrong file format, expected four lines, found %d", expected.length)
      );
    }
    commitHash = expected[0];
    timestamp = Timestamp.ofTimeMicroseconds(Long.parseLong(expected[1]));
    name = expected[2];
    status = BuilderStatus.valueOf(expected[3]);
    logs = new ArrayList<>();

    for (int i = 4; i < expected.length; i += 2) {
      logs.add(new Log(expected[i], expected[i + 1]));
    }
  }

  public String getCommitHash() {
    return commitHash;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public String getName() {
    return name;
  }

  public BuilderStatus getStatus() {
    return status;
  }

  public List<Log> getLogs() {
    return logs;
  }

  public String getContent() {
    return content;
  }
}
