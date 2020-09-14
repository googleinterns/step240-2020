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

import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.datatypes.BuilderIndex;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Repository;

/**
 * A DataRepository implementation backed up by Google Datastore.
 * Each database entry is modeled by the {@link #BuildInfo BuildInfo} class.
 * The relevant fields for the database are:
 *    - Kind: "revision"
 *    - Key: commit hash
 *
 * Useful links:
 * - https://googleapis.dev/java/google-cloud-datastore/latest/index.html
 * - https://googleapis.dev/java/spring-cloud-gcp/1.2.2.RELEASE/index.html
 * - https://cloud.google.com/datastore/docs/concepts/
 * - https://spring.io/projects/spring-cloud-gcp#overview
 */
@Repository
public class DatastoreRepository implements DataRepository {
  @Autowired
  private DatastoreTemplate storage;

  /**
   * {@inheritDoc}
   */
  public boolean createRevisionEntry(GitHubData entryData) throws IllegalArgumentException {
    if (entryData == null) {
      throw new IllegalArgumentException("entryData cannot be null");
    }

    if (getRevisionEntry(entryData.getCommitHash()) == null) {
      try {
        storage.save(new BuildInfo(entryData));
      } catch (DatastoreException e) {
        e.printStackTrace();
        System.err.println(e);
    
        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  public boolean updateRevisionEntry(BuildBotData updateData) throws IllegalArgumentException {
    if (updateData == null) {
      throw new IllegalArgumentException("entryData cannot be null");
    }

    BuildInfo associatedEntity = getRevisionEntry(updateData.getCommitHash());

    if (associatedEntity != null) {
      associatedEntity.addBuilder(updateData);

      try {
        storage.save(associatedEntity);
      } catch (DatastoreException e) {
        e.printStackTrace();
        System.err.println(e);
    
        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  public boolean deleteRevisionEntry(String commitHash) throws IllegalArgumentException {
    if (commitHash == null) {
      throw new IllegalArgumentException("commitHash cannot be null");
    }

    BuildInfo toBeDeleted = getRevisionEntry(commitHash);

    if (toBeDeleted != null) {
      try {
        storage.delete(toBeDeleted);
      } catch (DatastoreException e) {
        e.printStackTrace();
        System.err.println(e);

        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  public List<BuildInfo> getLastRevisionEntries(int number, int offset)
                                                         throws IllegalArgumentException {
    if (number < 0 || offset < 0) {
      throw new IllegalArgumentException("Both number and offset must be >= 0");
    }

    Query<Entity> query = Query.newEntityQueryBuilder()
                               .setKind("revision")
                               .setOrderBy(OrderBy.desc("timestamp"))
                               .setOffset(offset)
                               .setLimit(number)
                               .build();

    Iterable<BuildInfo> results = storage.query(query, BuildInfo.class).getIterable();

    List<BuildInfo> toBeReturned = new ArrayList<BuildInfo>();

    for (BuildInfo entity : results) {
      toBeReturned.add(entity);
    }

    return toBeReturned;
  }

  /**
   * {@inheritDoc}
   */
  public BuildInfo getRevisionEntry(String commitHash) throws IllegalArgumentException {
    if (commitHash == null) {
      throw new IllegalArgumentException("commitHash cannot be null");
    }

    return storage.findById(commitHash, BuildInfo.class);
  }

  /**
   * {@inheritDoc}
   */
  int getBuildbotIndex(String buildbotName) throws IllegalArgumentException, NotBoundException {
    if (buildbotName == null) {
      throw new IllegalArgumentException("buildbotName cannot be null");
    }

    BuilderIndex associatedEntity = storage.findById(buildbotName, BuilderIndex.class);

    if (associatedEntity == null) {
      throw new NotBoundException("buildbotName not bound to any index");
    }

    return associatedEntity.getIndex();
  }

  /**
   * {@inheritDoc}
   */
  void setBuildbotIndex(String buildbotName, int newValue) throws IllegalArgumentException,
                                                                  IndexOutOfBoundsException {
    if (buildbotName == null) {
      throw new IllegalArgumentException("buildbotName cannot be null");
    }

    BuilderIndex associatedEntity = storage.findById(buildbotName, BuilderIndex.class);

    if (associatedEntity == null || newValue <= associatedEntity.getIndex()) {
      throw new IndexOutOfBoundsException("newValue cannot be lower than or equal the previous index");
    }

    associatedEntity.setIndex(newValue);
    storage.save(associatedEntity);
  }
}
