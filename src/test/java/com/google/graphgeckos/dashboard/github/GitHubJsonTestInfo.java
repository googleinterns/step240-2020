package com.google.graphgeckos.dashboard.github;

import com.google.graphgeckos.dashboard.AbstractJsonTestInfo;

/**
 * Represents input and expected output for a test. Used in {@see GitHubControllerTest}.
 */
public class GitHubJsonTestInfo extends AbstractJsonTestInfo {

  // Expected output fields. See com.google.graphgeckos.dashboard.GitHubData class to learn more.
  private String branch;
  private String commitHash;
  private String timestamp;
  private String repositoryLink;

  /**
   * {@inheritDoc}
   */
  public GitHubJsonTestInfo(String testName) {
    super(testName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getPath() {
    return "src/test/resources/jsons/github/";
  }

  /**
   * {@inheritDoc}
   * Values come in the following order: branch, commit hash, timestamp, repository link.
   *
   * @param expected Lines of the expected output file for the test.
   */
  @Override
  protected void assignExpectedValues(String[] expected) {
    if (expected.length < 4) {
      throw new IllegalArgumentException(
        String.format("Wrong file format, expected four lines, found %d", expected.length));
    }
    branch = expected[0];
    commitHash = expected[1];
    timestamp = expected[2];
    repositoryLink = expected[3];
  }

  /**
   * Returns expected repository link.
   */
  public String getRepositoryLink() {
    return repositoryLink;
  }

  /**
   * Returns expected timestamp.
   */
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * Returns commit hash.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Returns branch name.
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Returns test input.
   */
  public String getContent() {
    return content;
  }

}
