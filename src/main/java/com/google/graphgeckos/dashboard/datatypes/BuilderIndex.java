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
import org.springframework.data.annotation.Id;

/**
 * This class encapsulates the information needed to retain the last revision index
 * of a builder. Since builders do not compile all revisions sequentially, they maintain
 * an internal index of compiled revisions, which differs on a builder by builder basis.
 * Used as a Datastore entity inside DatastoreRepository.
 */
@Entity(name = "index")
public class BuilderIndex {
  @Id
  @Field(name = "builderName")
  private String name;

  @Field(name = "timestamp")
  private int index;

  /**
   * Used only by Spring GCP.
   */
  public BuilderIndex() { }

  /**
   * Constructs the BuilderIndex instance to be stored inside GCD.
   */
  public BuilderIndex(String name, int index) {
    this.name = name;
    this.index = index;
  }

  /**
   * @return the name of the builder.
   */
  public String getName() {
    return name;
  }

  /**
   * @return the index of the builder.
   */
  public int getIndex() {
    return index;
  }

  /**
   * @param name the new name of the builder
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param index the new index of the builder
   */
  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof BuilderIndex)) {
      return false;
    }
    
    if (o == this) {
      return true;
    }

    BuildInfo other = (BuilderIndex) o;
    return this.name == other.name && this.index == other.index;
  }
}
