package com.google.graphgeckos.dashboard.github;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Represents input and expected output for a test. Used in {@see GitHubControllerTest}.
 */
public class GitHubJsonTestInfo {

  // Path to the folder with the files (inputs and expected outputs) for testing.
  private static final String PATH = "src/test/resources/jsons/";

  // The file names for one test differ only in prefixes. E.g input_real_json.txt and output_real_json.txt.
  private static final String INPUT_PREFIX = "input_";
  private static final String EXPECTED_PREFIX = "output_";

  // Inputs and expected outputs are stored as txt files.
  private static final String FILE_FORMAT = ".txt";

  // Test input (json).
  private String content;

  // Expected output fields. See com.google.graphgeckos.dashboard.GitHubData class to learn more.
  private String branch;
  private String commitHash;
  private String timestamp;
  private String repositoryLink;

  private String readFile(String filePath) throws IOException {
    return Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));
  }

  /**
   * The expected output of each test is stored as a txt file with four lines. Values come in the following order:
   * branch, commit hash, timestamp, repository link.
   * @param expected Lines of the expected output file for the test.
   */
  private void assignExpectedValues(String[] expected) {
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
    try {
      content = readFile(PATH + INPUT_PREFIX + testName + FILE_FORMAT);
    } catch (IOException e) {
      System.out.println("File not found:" + e.getMessage());
    }
    try {
      String[] expected = readFile(PATH + EXPECTED_PREFIX + testName + FILE_FORMAT).split("\n");
      assignExpectedValues(expected);
    } catch (IOException e) {
      System.out.println("File not found:" + e.getMessage());
    }
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
