package com.google.graphgeckos.dashboard.fetchers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitData {

  // Git commit hash
  @JsonProperty("after")
  private final String commitHash;

  @JsonProperty("ref")
  private final String branch;

  private String timestamp;

  private String repositoryLink;

  @JsonProperty("repository")
  private void unpackRepositoryData(Map<String, Object> repository) {
    timestamp = repository.get("updated-at").toString();
    repositoryLink = repository.get("html_url").toString();
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

  public String getRepositoryName() {
    return repositoryName;
  }
}
