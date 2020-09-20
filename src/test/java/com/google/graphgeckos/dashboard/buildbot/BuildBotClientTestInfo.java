package com.google.graphgeckos.dashboard.buildbot;

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
  private String timestamp;
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
    if (expected.length < 4) {
      throw new IllegalArgumentException(
        String.format("Wrong file format, expected four lines, found %d", expected.length)
      );
    }
    logs = new ArrayList<>();
    commitHash = expected[0];
    timestamp = expected[1];
    name = expected[2];
    status = BuilderStatus.valueOf(expected[3]);

    for (int i = 4; i < expected.length; i += 2) {
      logs.add(new Log(expected[i], expected[i + 1]));
    }
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getTimestamp() {
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
}
