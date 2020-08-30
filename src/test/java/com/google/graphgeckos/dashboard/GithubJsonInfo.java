package com.google.graphgeckos.dashboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GithubJsonInfo {

  private static final String PATH = "src/test/resources/jsons/";
  private static final String TEST_PREFIX = "test_";
  private static final String EXPECTED_PREFIX = "expected_";

  private String content;
  private String branch;
  private String commitHash;
  private String timestamp;
  private String repositoryLink;

  private String readFile(String fileName) throws IOException {
    Path path = Paths.get(fileName);
    return Files.lines(path).collect(Collectors.joining("\n"));
  }

  private void assignExpectedValues(String[] rawExpected) {
    if (rawExpected.length < 4) {
      throw new IllegalArgumentException(
        String.format("Wrong file format, expected four line, found %d", rawExpected.length));
    }
    branch = rawExpected[0];
    commitHash = rawExpected[1];
    timestamp = rawExpected[2];
    repositoryLink = rawExpected[3];
  }

  public GithubJsonInfo(String testName) {
    try {
      content = readFile(PATH + TEST_PREFIX + testName);
    } catch (IOException e) {
      System.out.println("File not found:" + e.getMessage());
    }
    try {
      String[] expected = readFile(PATH + EXPECTED_PREFIX + testName).split("\n");
      assignExpectedValues(expected);
    } catch (IOException e) {
      System.out.println("File not found:" + e.getMessage());
    }
  }

  public String getRepositoryLink() {
    return repositoryLink;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getBranch() {
    return branch;
  }

  public String getContent() {
    return content;
  }
}
