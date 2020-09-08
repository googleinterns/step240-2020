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
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;

/**
 * Contains the information retrieved from a single build bot. It is used as a member of BuildInfo...
 * of {@link #BuildInfo BuildInfo}, and for converting ParsedBuildbotData objects
 * to Builder objects, used by BuildInfo.
 */
@Entity(name = "builder")
public class Builder {

  @Field(name = "name")
  private String name;

  // The logs of each compilation stage, stored as described by {@link #Log Log}.
  @Field(name = "logs")
  private List<Log> logs;

  // Builder compilation status, as described by {@link #BuilderStatus BuilderStatus}.
  @Field(name = "status")
  private BuilderStatus status;

  Builder() {
    this.name = null;
    this.logs = null;
    this.status = null;
  }

  /**
   * Converts a {@link #ParsedBuildbotData ParsedBuildbotData} object to a Builder object.
   *
   * @throws IllegalArgumentException if {@code botData} is null
   */
  Builder(ParsedBuildbotData botData) throws IllegalArgumentException {
    if (botData == null) {
      throw new IllegalArgumentException("botData cannot be null in Builder constructor");
    }

    this.name = botData.getBuilderName();
    this.logs = new ArrayList<>(botData.getLogs());
    this.status = botData.getStatus();
  }

  Builder(String name, List<Log> logs, BuilderStatus status) {
    this.name = name;
    this.logs = new ArrayList<>(logs);
    this.status = status;
  }

  /**
   * Returns the name of the builder bot. Cannot be null.
   */
  public String getName() {
    return name;
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

  void setName(String name) {
    this.name = name;
  }

  void setLogs(List<Log> logs) {
    this.logs = new ArrayList<>(logs);
  }

  void setStatus(BuilderStatus status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
  
    if (!(o instanceof Builder)) {
      return false;
    }
    
    Builder other = (Builder) o;
    return this.name.equals(other.name) && this.logs.equals(other.logs) &&
           this.status.equals(other.status);
  }

}
