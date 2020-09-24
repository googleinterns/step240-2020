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

package com.google.graphgeckos.dashboard.datatypes;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;

/**
 * Contains the log information from a single compilation stage from a given build bot.
 */
@Entity(name = "buildLog")
public class Log {

  // Log type (e.g "stdio")
  @Field(name = "type")
  private String type;

  // Log link (e.g "http://lab.llvm.org:8011/builders/mlir-nvidia/builds/6403/logs/stdio")
  @Field(name = "link")
  private String link;

  /**
   * Used only by Spring GCP.
   */
  public Log() {}

  public Log(String type, String link) {
    this.type = type;
    this.link = link;
  }

  public String getType() {
    return type;
  }

  public String getLink() {
    return link;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof Log)) {
      return false;
    }

    Log other = (Log) o;
    return type.equals(other.type) && link.equals(other.link);
  }

}
