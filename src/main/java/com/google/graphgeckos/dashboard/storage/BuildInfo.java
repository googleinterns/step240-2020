package com.google.graphgeckos.dashboard.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the response json with the aggregated buildbot data.
 */
public class BuildInfo {

  // Git commit hash
  private final String commitHash;
  // time of the Git commit push
  private final Date timestamp;
  // link to to the repository
  private final String repository;
  // name of the branch
  private final String branch;

  // builders
  private final List<Builder> builders;

  public BuildInfo(final String commitHash, final Date timestamp, final String repository,
                   final String branch, final List<Builder> builders) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.repository = repository;
    this.branch = branch;
    this.builders = new ArrayList<>(builders);
  }

  /**
   * Returns Git commit hash of the tested changes commit.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Returns time of when the tested commit was pushed (the time is fetched from the Git API).
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * Returns link to to the Git repository where the tested changes were made.
   */
  public String getRepository() {
    return repository;
  }

  /**
   * Returns name of the Git branch where the tested changes were made.
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Returns builders which tested the changes.
   */
  public List<Builder> getBuilders() {
    return builders;
  }

}
