package com.google.graphgeckos.dashboard.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubData {

  private String branch;

  private String commitHash;
  
  private Timestamp timestamp;

  private String repositoryLink;

  /**
   * Extracts commit id (also known as commit hash) and timestamp nested fields from the data of the head_commit
   * field.
   * @param headCommit representation of the head_commit field of the GitHub API request json.
   */
  @JsonProperty("head_commit")
  private void unpackHeadCommit(Map<String, Object> headCommit) {
    commitHash = headCommit.get("id").toString();
    timestamp = Timestamp.of(new Date(headCommit.get("timestamp").toString()));
  }

  /**
   * Extracts link to the working repository (html_url field value) from the data of the repository field.
   * @param repository representation of the repository field of the GitHub API request json.
   */
  @JsonProperty("repository")
  private void extractRepositoryLink(Map<String, Object> repository) {
    repositoryLink = repository.get("html_url").toString();
  }

  /**
   * Extracts the name of the branch from the reference.
   * @param ref Git reference.
   */
  @JsonProperty("ref")
  private void extractBranch(String ref) {
    String[] refComponents = ref.split("/");
    branch = refComponents[refComponents.length - 1];
  }

  public GitHubData(String commitHash, Timestamp timestamp, String branch) {
    this.commitHash = commitHash;
    this.timestamp = timestamp;
    this.branch = branch;
  }

  /**
   * Returns name of the Git branch, where the tested changes were made.
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Returns Git commit hash of the last commit.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Returns time of when the commit was pushed.
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Returns link to the Git repository, where the last commit was made.
   */
  public String getRepositoryLink() {
    return repositoryLink;
  }

}
