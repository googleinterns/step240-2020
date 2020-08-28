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
 * Represents buildbot statuses: "failed" or "passed".
 */
@Entity(name = "buildStatus")
public enum BuilderStatus {
  FAILED("failed"),
  PASSED("passed");

  private final String status;

  BuilderStatus(String status) {
    this.status = status;
  }

  /**
   * Returns the String representation of the status.
   *
   * @return status as a String.
   */
  public String getStatus() {
    return status;
  }

}
