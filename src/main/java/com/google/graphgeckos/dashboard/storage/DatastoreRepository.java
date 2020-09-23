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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DatastoreOptions.DefaultDatastoreFactory;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.datatypes.BuilderIndex;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.cloud.gcp.data.datastore.core.convert.DatastoreServiceObjectToKeyFactory;
import org.springframework.cloud.gcp.data.datastore.core.convert.DefaultDatastoreEntityConverter;
import org.springframework.cloud.gcp.data.datastore.core.mapping.DatastoreMappingContext;
import org.springframework.lang.NonNull;
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
  private DatastoreTemplate storage;

  /**
   * Constructs a DatastoreRepository which uses {@code underlyingStorage} as the database
   * interface which ultimately handles the requests.
   * 
   * @throws NullPointerException if {@code underlyingStorage} is null
   */
  public DatastoreRepository(@NonNull Datastore underlyingStorage) {
    checkNotNull(underlyingStorage);

    Supplier<Datastore> supplier = () -> underlyingStorage;

    DatastoreMappingContext mappingContext = new DatastoreMappingContext();

    DatastoreServiceObjectToKeyFactory objectToKeyFactory =
        new DatastoreServiceObjectToKeyFactory(supplier);

    DefaultDatastoreEntityConverter entityConverter =
        new DefaultDatastoreEntityConverter(mappingContext, objectToKeyFactory);

    storage = new DatastoreTemplate(supplier, entityConverter, mappingContext, objectToKeyFactory);
  }

  public DatastoreRepository() {
    this(new DefaultDatastoreFactory().create(DatastoreOptions.getDefaultInstance()));
  }

  /**
   * {@inheritDoc}
   *
   * @throws NullPointerException if {@code entryData} is null.
   */
  @Override
  public boolean createRevisionEntry(@NonNull GitHubData entryData) {
    checkNotNull(entryData);

    if (getRevisionEntry(entryData.getCommitHash()) != null) {
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
   *
   * @throws NullPointerException if {@code updateData} is null.
   */
  @Override
  public boolean updateRevisionEntry(@NonNull BuildBotData updateData) {
    checkNotNull(updateData);

    BuildInfo associatedEntity = getRevisionEntry(updateData.getCommitHash());

    if (associatedEntity == null) {
      return false;
    }

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
   *
   * @throws NullPointerException if {@code commitHash} is null.
   */
  @Override
  public boolean deleteRevisionEntry(@NonNull String commitHash) {
    checkNotNull(commitHash);

    BuildInfo toBeDeleted = getRevisionEntry(commitHash);

    if (toBeDeleted == null) {
      return true;
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
   * 
   * @throws IllegalArgumentException if either number or offset are < 0
   */
  @Override
  public List<BuildInfo> getLastRevisionEntries(int number, int offset) {
    checkArgument(number >= 0, "number must be >= 0");
    checkArgument(offset >= 0, "offset must be >= 0");

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
   *
   * @throws NullPointerException if the {@code commitHash} is null
   */
  @Override
  public BuildInfo getRevisionEntry(@NonNull String commitHash) {
    checkNotNull(commitHash);

    return storage.findById(commitHash, BuildInfo.class);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException if {@code buildbotName} is null
   * @throws BuildbotNotFound if there is no "index" entry related to the name provided
   */
  @Override
  public int getBuildbotIndex(@NonNull String buildbotName) {
    checkNotNull(buildbotName);

    BuilderIndex associatedEntity = storage.findById(buildbotName, BuilderIndex.class);

    if (associatedEntity == null) {
      throw new BuildbotNotFoundException("buildbotName not bound to any index");
    }

    return associatedEntity.getIndex();
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException if {@code buildbotName} is null or there is no "index" entry
   *      with that key
   * @throws IndexOutOfBoundsException if newValue is lower or equal than the previous
   *      recorded value
   */
  @Override
  public void setBuildbotIndex(@NonNull String buildbotName, int newValue) {
    checkNotNull(buildbotName);

    BuilderIndex associatedEntity = storage.findById(buildbotName, BuilderIndex.class);

    checkNotNull(associatedEntity, "no associated entity with the provided name");

    checkArgument(newValue <= associatedEntity.getIndex(),
                               "newValue cannot be lower than or equal the previous index");

    associatedEntity.setIndex(newValue);
    storage.save(associatedEntity);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException if {@code name} is null
   * @throws IndexOutOfBoundsException if value is negative when creating a new entry, or if value
   *      is lower or equal than the previous recorded value
   */
  @Override
  public void registerNewBuildbot(@NonNull String buildbotName, int value) {
    checkNotNull(buildbotName);

    BuilderIndex associatedEntity = storage.findById(buildbotName, BuilderIndex.class);

    checkArgument(value <= associatedEntity.getIndex(),
                               "value cannot be <= than the previous known value");
    checkArgument(value < 0, "value cannot be negative");
    
    BuilderIndex newEntity = new BuilderIndex(buildbotName, value);
    storage.save(newEntity);
  }
}
