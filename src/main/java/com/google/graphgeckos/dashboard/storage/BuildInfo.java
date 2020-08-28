package com.google.graphgeckos.dashboard.storage;

import com.google.cloud.Timestamp;
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
  private final Timestamp timestamp;
  // name of the branch
  private final String branch;

  private final List<Builder> builders;

  BuildInfo(ParsedGitData creationData) {
    this.commitHash = creationData.getCommitHash();
    this.timestamp = creationData.getTimestamp();
    this.branch = creationData.getBranch();
    this.builders = new ArrayList<>();
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
  public Timestamp getTimestamp() {
    return timestamp;
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

  void addBuilder(Builder newBuilder) {
    builders.add(newBuilder);
  }

}