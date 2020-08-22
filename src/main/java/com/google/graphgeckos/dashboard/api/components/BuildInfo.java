package com.google.graphgeckos.dashboard.api.components;

import java.util.Arrays;
import java.util.List;

public class BuildInfo {
  private final String commitHash;
  private final String timeStamp;
  private final String repository;
  private final String branch;

  private final List<Builder> builders;

  public BuildInfo(String commitHash, String timeStamp, String repository,
                   String branch, Builder[] builders) {
    this.commitHash = commitHash;
    this.timeStamp = timeStamp;
    this.repository = repository;
    this.branch = branch;
    this.builders = Arrays.asList(builders);
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
