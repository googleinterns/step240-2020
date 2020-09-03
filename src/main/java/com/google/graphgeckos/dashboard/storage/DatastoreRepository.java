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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/**
 * A DataRepository implementation backed up by Google Datastore.
 * Datastore. Each database entry is modeled by the {@link BuildInfo} class.
 * The relevant fields for the database are:
 * - Kind: "revision"
 * - Key: commit hash
 * <p>
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
  @Override
  public boolean createRevisionEntry(@NonNull GitHubData entryData) {
    if (hasRevisionEntry(entryData.getCommitHash())) {
      return false;
    }

    try {
      storage.save(new BuildInfo(entryData));
    } catch (DatastoreException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean updateRevisionEntry(@NonNull Builder updateData) {
    BuildInfo associatedEntity = getRevisionEntry(updateData.getCommitHash());

    if (associatedEntity == null) {
      return false;
    }

    associatedEntity.addBuilder(updateData);
    try {
      storage.save(associatedEntity);
    } catch (DatastoreException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean deleteRevisionEntry(@NonNull String commitHash) {
    BuildInfo toBeDeleted = getRevisionEntry(commitHash);

    if (toBeDeleted == null) {
      return false;
    }

    try {
      storage.delete(toBeDeleted);
    } catch (DatastoreException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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

    List<BuildInfo> toBeReturned = new ArrayList<>();
    storage.query(query, BuildInfo.class).getIterable().forEach(toBeReturned::add);
    return toBeReturned;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BuildInfo getRevisionEntry(@NonNull String commitHash) {
    return storage.findById(commitHash, BuildInfo.class);
  }

}
