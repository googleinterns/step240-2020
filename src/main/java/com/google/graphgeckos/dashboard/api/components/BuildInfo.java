package com.google.graphgeckos.dashboard.api.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the response json with the
 * aggregated buildbot data.
 */
public class BuildInfo {
  private final String commitHash;
  private final String timeStamp;
  private final String repository;
  private final String branch;


  private final List<Builder> builders;

  public BuildInfo(String commitHash, String timeStamp, String repository,
                   String branch, List<Builder> builders) {
    this.commitHash = commitHash;
    this.timeStamp = timeStamp;
    this.repository = repository;
    this.branch = branch;
    this.builders = new ArrayList<>(builders);
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getTimeStamp() {
    return timeStamp;
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
