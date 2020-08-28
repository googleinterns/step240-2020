package com.google.graphgeckos.dashboard.storage;

import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Unindexed;
import org.springframework.data.annotation.Id;

/**
 * Represents the response json with the aggregated buildbot data.
 */
@Entity(name = "revision")
public class BuildInfo {

  // Git commit hash
  @Id
  @Field(name = "commitHash")
  private final String commitHash;
  // time of the Git commit push
  @Field(name = "timestamp")
  private final Timestamp timestamp;
  // name of the branch
  @Field(name = "branch")
  private final String branch;

  @Field(name = "builders")
  @Unindexed
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
