package com.google.graphgeckos.dashboard.api.components;


/**
 * Represents buildbot statuses: "failed" or "passed"
 */
public enum BuilderStatus {
  FAILED("failed"),
  PASSED("passed");

  // String representation of the status
  private final String status;

  BuilderStatus(String status) {
    this.status = status;
  }

  /**
   * Returns String representation of the status
   */
  public String getStatus() {
    return status;
  }

}
