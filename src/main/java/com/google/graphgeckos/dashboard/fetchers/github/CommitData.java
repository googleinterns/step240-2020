package com.google.graphgeckos.dashboard.fetchers.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitData {

  // Name of the branch.
  private String branch;

  /**
   * Extracts the name of the branch from the reference.
   * @param ref Git reference.
   */
  @JsonProperty("ref")
  private void extractBranch(String ref) {
    String[] refComponents = ref.split("/");
    branch = refComponents[refComponents.length - 1];
  }

  // Git commit hash.
  private String commitHash;

  // Time of the Git commit push.
  private String timestamp;

  /**
   * Extracts commit id (also known as commit hash) and timestamp nested fields from the data of the head_commit
   * field.
   * Assigns the id value to {@code commitHash} and the timestamp value to {@code timestamp}.
   * @param headCommit representation of the head_commit field of the GitHub API request json.
   */
  @JsonProperty("head_commit")
  private void unpackHeadCommit(Map<String, Object> headCommit) {
    commitHash = headCommit.get("id").toString();
    timestamp = headCommit.get("timestamp").toString();
  }

  // Link to to the repository.
  private String repositoryLink;

  /**
   * Extracts link to the working repository (html_url field value) from the data of the repository field.
   * Assigns the extracted value to {@code repositoryLink}.
   * @param repository representation of the repository field of the GitHub API request json.
   */
  @JsonProperty("repository")
  private void extractRepositoryLink(Map<String, Object> repository) {
    repositoryLink = repository.get("html_url").toString();
  }

  CommitData(String commitHash, String branch) {
    this.commitHash = commitHash;
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
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * Returns link to the Git repository, where the last commit was made.
   */
  public String getRepositoryLink() {
    return repositoryLink;
  }

}
