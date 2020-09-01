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

import java.util.ArrayList;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

/**
 * Contains the information retrieved from a single build bot. It is used as a member of BuildInfo...
 * of {@link #BuildInfo BuildInfo}, and for converting ParsedBuildbotData objects
 * to Builder objects, used by BuildInfo.
 */
@Entity(name = "builder")
public class Builder {

  private final String builderName;

  // The logs of each compilation stage, stored as described by {@link #Log Log}.
  private final List<Log> logs;

  // Builder compilation status, as described by {@link #BuilderStatus BuilderStatus}.
  private final BuilderStatus status;

  /**
   * Converts a {@link #ParsedBuildbotData ParsedBuildbotData} object to a Builder object.
   * The fields should be sanitized before this operation, as no field in Builder may be
   * null.
   */
  Builder(ParsedBuildBotData botData) {
    this.builderName = botData.getBuilderName();
    this.logs = new ArrayList<>(botData.getLogs());
    this.status = botData.getStatus();
  }

  /**
   * Returns the name of the builder bot. Cannot be null.
   */
  public String getBuilderName() {
    return builderName;
  }

  /**
   * Returns the list of logs for each compilation stage. Cannot be null.
   */
  public List<Log> getLogs() {
    return logs;
  }

  /**
   * Returns the compilation status of the builder. Cannot be null.
   */
  public BuilderStatus getStatus() {
    return status;
  }

}
