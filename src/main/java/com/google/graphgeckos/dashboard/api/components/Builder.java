package com.google.graphgeckos.dashboard.api.components;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the information about a single buildbot.
 */
public class Builder {
  private final String builderName;
  private final List<Log> logs;

  //"failed" or "passed"
  //TODO for mashasamsikova: add java enum representation for these states
  private final String status;

  public Builder(String builderName, Log[] logs, String status) {
    this.builderName = builderName;
    this.logs = Arrays.asList(logs);
    this.status = status;
  }

  public String getBuilderName() {
    return builderName;
  }

  public List<Log> getLogs() {
    return logs;
  }

  public String getStatus() {
    return status;
  }

}
