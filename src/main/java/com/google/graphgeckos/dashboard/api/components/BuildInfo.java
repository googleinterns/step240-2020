package com.google.graphgeckos.dashboard.api.components;

import java.util.ArrayList;
import java.util.List;

// Represents the response json with the aggregated buildbot data.
public class BuildInfo {

  // git commit hash of the tested changes commit
  private final String commitHash;
  /*
   the relative time (time since the application was started),
   allows to sort received build info in the descending order (latest goes first)
   */
  private final String timestamp;
  // link to to the git repository where the tested changes were made
  private final String repository;
  // name of the git branch where the tested changes were made
  private final String branch;

  // builders which tested the changes
  private final List<Builder> builders;

  public BuildInfo(String commitHash, String timestamp, String repository,
                   String branch, List<Builder> builders) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.repository = repository;
    this.branch = branch;
    this.builders = new ArrayList<>(builders);
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getRepository() {
    return repository;
  }

  public String getBranch() {
    return branch;
  }

  public List<Builder> getBuilders() {
    return builders;
  }

}
