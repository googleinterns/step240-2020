package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.AbstractJsonTestInfo;
import com.google.graphgeckos.dashboard.datatypes.Log;
import java.util.ArrayList;
import java.util.List;

public class BuildBotTestInfo extends AbstractJsonTestInfo  {

  /**
   * {@inheritDoc}
   */
  public BuildBotTestInfo(String testName) {
    super(testName);
  }

  /** Expected output fields.
   * {@link com.google.graphgeckos.dashboard.datatypes.BuildBotData}
   */
  private String name;
  private String status;
  private String commitHash;
  private String timestamp;
  private List<Log> logs = new ArrayList<>();

  @Override
  protected String getPath() {
    return "src/test/resources/jsons/buildbots/";
  }

  /**
   * {@inheritDoc}
   * Values come in the following order: BuildBot name, status, commitHash, timestamp,
   * logs (expected[i] contains type of the log, expected[i + 1] contains
   * link to the log.
   */
  @Override
  protected void assignExpectedValues(String[] expected) {
    name = expected[0];
    status = expected[1];
    commitHash = expected[2];
    timestamp = expected[3];

    for (int i = 4; i < expected.length; i += 2) {
      logs.add(new Log(expected[i], expected[i + 1]));
    }
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public List<Log> getLogs() {
    return logs;
  }
}
