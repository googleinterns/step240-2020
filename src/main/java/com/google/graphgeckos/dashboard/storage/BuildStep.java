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

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

/**
 * Contains the log information from a single compilation stage from a given build bot.
 */
@Entity(name = "buildStep")
public class BuildStep {

  // BuildStep name (e.g "clean-src-dir")
  private final String name;

  // BuildStep link (e.g "http://lab.llvm.org:8011/builders/mlir-nvidia/builds/6403/logs/stdio")
  private final String link;

  /**
   * Constructs an instance of BuildStep. Both name and link can be null.
   */
  BuildStep(String name, String link) {
    this.name = name;
    this.link = link;
  }

  /**
   * Returns the name of the log as a String. Can be null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a link to the full version of the log. Can be null.
   */
  public String getLink() {
    return link;
  }

}
