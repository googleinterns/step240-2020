package com.google.graphgeckos.dashboard;

/**
 * Represents input and expected output for a test. Used in {@see GitHubControllerTest}.
 */
public class GitHubJsonTestInfo extends AbstractJsonTestInfo {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getPath() {
    return "src/test/resources/jsons/";
  }

  // Expected output fields. See com.google.graphgeckos.dashboard.GitHubData class to learn more.
  private String branch;
  private String commitHash;
  private String timestamp;
  private String repositoryLink;

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
   * @param testName Common part of the input and output file names. E.g the test name "real_json" is expected
   *                to have input_real_json.txt file with input json and output_real_json.txt with expected output.
   */
  public GitHubJsonTestInfo(String testName) {
    super(testName);
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
