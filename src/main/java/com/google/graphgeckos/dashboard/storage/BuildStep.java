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
  private final <List>Log logs;

  /**
   * Name, text and logs can be null.
   * @param name - name of the build step.
   * @param text - output text related to step's build status.
   * @param isFinished - indicates if the step has been completed.
   * @param isStarted - indicates if the step has been initiated.
   * @param logs - the logs associated with the build step.
   */
  BuildStep(String name, String[] text, boolean isFinished, boolean isStarted, Log[] logs) {
    this.name = name;
    this.text = Arrays.toString(text);
    this.isFinished = isFinished;
    this.isStarted = isStarted;
    this.logs = Arrays.asList(logs);
  }

  /**
   * Returns the name of the build step. Cannot be null.
   */
  @NonNull
  public String getName() {
    return this.name;
  }

  /**
   * Returns output text related to the build status. Can be null.
   */
  public String getText() {
    return this.text;
  }

  /**
   * Returns the order of the step. Value always >= 0.
   */
  public int getStepNumber() {
    return this.stepNumber;
  }

  /**
   * Returns a boolean indicating if the step has been started.
   */
  public int isStarted() {
    return this.isStarted;
  }

  /**
   * Returns a boolean indicating if the step has been completed.
   */
  public int isFinished() {
    return this.isFinished;
  }

  /**
   * Returns an array of the logs associated with this step. Can be null.
   */
  public Log[] getLogs() {
    return this.logs;
  }
}
