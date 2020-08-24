package com.google.graphgeckos.dashboard.api.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the response json with the aggregated buildbot data.
 */
public class BuildInfo {

  // git commit hash
  private final String commitHash;
  // time of receiving build info
  private final Date timestamp;
  // link to to the repository
  private final String repository;
  // name of the branch
  private final String branch;

  // builders
  private final List<Builder> builders;

  public BuildInfo(String commitHash, Date timestamp, String repository,
                   String branch, List<Builder> builders) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.repository = repository;
    this.branch = branch;
    this.builders = new ArrayList<>(builders);
  }


  /**
   * Returns git commit hash of the tested changes commit
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Returns time of receiving build info
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * Returns link to to the git repository where the tested changes were made
   */
  public String getRepository() {
    return repository;
  }

  /**
   * Returns name of the git branch where the tested changes were made
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Returns builders which tested the changes
   */
  public List<Builder> getBuilders() {
    return builders;
  }

}
