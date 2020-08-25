package com.google.graphgeckos.dashboard.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the information about a single buildbot.
 */
public class Builder {
  // name of the builder
  private final String builderName;
  // builder logs
  private final List<Log> logs;

  // builder status, FAILED or PASSED
  private final BuilderStatus status;

  Builder(final String builderName, final List<Log> logs, final BuilderStatus status) {
    this.builderName = builderName;
    this.logs = new ArrayList<>(logs);
    this.status = status;
  }

  /**
   * Returns name of the builder.
   */
  public String getBuilderName() {
    return builderName;
  }

  /**
   * Returns list of logs of the builder.
   */
  public List<Log> getLogs() {
    return logs;
  }

  /**
   * Returns status of the builder (FAILED or PASSED).
   */
  public BuilderStatus getStatus() {
    return status;
  }

}
