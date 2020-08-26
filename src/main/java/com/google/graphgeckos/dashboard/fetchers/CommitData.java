package com.google.graphgeckos.dashboard.fetchers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitData {
  
  @JsonProperty("after")
  private final String commitHash;

  private String timestamp;

  @JsonProperty("ref")
  private final String branch;

  @JsonProperty("repository")
  private void unpackTimestamp(Map<String, Object> repository) {
    timestamp = repository.get("updated-at").toString();
  }

  CommitData(String commitHash, String branch) {
    this.commitHash = commitHash;
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

  @Override
  public String toString() {
    return String.format("branch: %s, comitHash: %s, timestamp, %s", branch, commitHash, timestamp);
  }
}
