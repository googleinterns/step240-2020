package com.google.graphgeckos.dashboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;


/** Provides functionality to perform golden file testing. */
public abstract class AbstractJsonTestInfo {

  /** Returns path to the folder with the files (inputs and expected outputs) for testing. */
  protected abstract String getPath();

  /** The file names for one test differ only in prefixes. E.g input_real_json.txt and output_real_json.txt. */
  protected static final String INPUT_PREFIX = "input_";
  protected static final String EXPECTED_PREFIX = "output_";

  /** Inputs and expected outputs are stored as txt files. */
  protected static final String FILE_FORMAT = ".txt";

  /** Test input (json). */
  protected String content;

  /**
   * @param testName Common part of the input and output file names. E.g the test name "real_json" is expected
   *                to have input_real_json.txt file with input json and output_real_json.txt with expected output.
   */
  public AbstractJsonTestInfo(String testName) {
    try {
      content = readFile(getPath() + INPUT_PREFIX + testName + FILE_FORMAT);
    } catch (IOException e) {
      System.out.println("File not found:" + e.getMessage());
    }
    try {
      String[] expected = readFile(getPath() + EXPECTED_PREFIX + testName + FILE_FORMAT).split("\n");
      assignExpectedValues(expected);
    } catch (IOException e) {
      System.out.println("File not found:" + e.getMessage());
    }
  }

  protected String readFile(String filePath) throws IOException {
    return Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));
  }

  /**
   * The expected output for each test is stored as a txt file. One line for each field.
   * The order of the values is described in inherited classes.
   *
   * @param expected Lines of the expected output file for the test.
   */
  protected abstract void assignExpectedValues(String[] expected);

}
