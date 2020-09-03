// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.graphgeckos.dashboard.storage;

import java.util.Arrays;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

/**
 * Contains the information for an individual step of a build.
 */
@Entity(name = "buildStep")
public class BuildStep {

  // Order of the step
  private final int stepNumber;

  // BuildStep name (e.g "clean-src-dir")
  private final String name;

  // BuildStep text (e.g "clean-src-dir skipped")
  private final String text;

  // Indicator of whether or not the step is complete
  private final boolean isFinished;

  // Indicator of whether or not the step has started
  private final boolean isStarted;

  // All the logs associated with this step
  private final Log[] logs;

  /**
   * Constructs an instance of BuildStep. Both name and text can be null.
   */
  BuildStep(String name, String[] text, boolean isFinished, boolean isStarted, Log[] logs) {
    this.name = name;
    this.text = Arrays.toString(text);
    this.isFinished = isFinished;
    this.isStarted = isStarted;
    this.logs = logs;
  }

  /**
   * Returns the name of the log as a String. Can be null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a text to the full version of the log. Can be null.
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the order of the step. Value always >= 0.
   */
  public int getStepNumber() {
    return stepNumber;
  }

  /**
   * Returns a boolean indicating if the step has been started.
   */
  public int isStarted() {
    return isStarted;
  }

  /**
   * Returns a boolean indicating if the step has been completed.
   */
  public int isFinished() {
    return isFinished;
  }

  /**
   * Returns an array of the logs associated with this step. Can be null.
   */
  public Log[] getLogs() {
    return logs;
  }
}
