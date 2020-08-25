package com.google.graphgeckos.dashboard.fetchers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitData {
  private final String commitHash;
  private final String timestamp;
  private final String branch;

  CommitData(String commitHash, String timestamp, String branch) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.branch = branch;
  }

  public String getBranch() {
    return branch;
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getTimestamp() {
    return timestamp;
  }
}
