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

import com.google.cloud.datastore.Datastore;
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
import java.util.function.Supplier;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.cloud.gcp.data.datastore.core.convert.DatastoreServiceObjectToKeyFactory;
import org.springframework.cloud.gcp.data.datastore.core.convert.DefaultDatastoreEntityConverter;
import org.springframework.cloud.gcp.data.datastore.core.mapping.DatastoreMappingContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
  private ApplicationContext context;
  private DatastoreTemplate storage;

  DatastoreRepository(Datastore underlyingStorage) {
    // context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
    // if (context == null) {
    //   throw new RuntimeException("NULL CONTEXT");
    // }

    Supplier<Datastore> supplier = () -> { return underlyingStorage; };

    DatastoreMappingContext mappingContext = new DatastoreMappingContext();
    //mappingContext.setApplicationContext(context);

    DatastoreServiceObjectToKeyFactory objectToKeyFactory =
        new DatastoreServiceObjectToKeyFactory(supplier);
        
    DefaultDatastoreEntityConverter entityConverter =
        new DefaultDatastoreEntityConverter(mappingContext, objectToKeyFactory);

    storage = new DatastoreTemplate(supplier, entityConverter, mappingContext, objectToKeyFactory);
  }

  /**
   * {@inheritDoc}
   */
  public boolean createRevisionEntry(ParsedGitData entryData) throws IllegalArgumentException {
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
  public boolean updateRevisionEntry(ParsedBuildbotData updateData) throws IllegalArgumentException {
    if (updateData == null) {
      throw new IllegalArgumentException("entryData cannot be null");
    }

    BuildInfo associatedEntity = getRevisionEntry(updateData.getCommitHash());

    if (associatedEntity != null) {
      associatedEntity.addBuilder(new Builder(updateData));

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
    }

    return true;
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
}
