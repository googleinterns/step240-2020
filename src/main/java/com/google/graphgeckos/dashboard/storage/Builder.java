package com.google.graphgeckos.dashboard.storage;

import java.util.ArrayList;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

/**
 * Represents the information about a single buildbot.
 */
@Entity(name = "builder")
public class Builder {
  // name of the builder
  private final String builderName;
  // builder logs
  private final List<Log> logs;

  // builder status, FAILED or PASSED
  private final BuilderStatus status;

  Builder(String builderName, List<Log> logs, BuilderStatus status) {
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
