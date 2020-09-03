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
import org.springframework.stereotype.Repository;

/**
 * A DataRepository implementation backed up by Google Datastore.
 * Datastore. Each database entry is modeled by the {@link #BuildInfo BuildInfo} class.
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
  private static final entryTtlInDays = 90;

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
      associatedEntity.addBuilder(updateData.toBuilder());

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
   * Queries the database for a given entry, that has the Id set to the provided commitHash.
   * Used to separate the usage from the actual Spring Datastore implementation.
   * Returns null when nothing was found.
   */
  public BuildInfo getRevisionEntry(String commitHash) {
    return storage.findById(commitHash, BuildInfo.class);
  }

  /**
   * Queries all "revision" type entries, and deletes all which are older than a specified
   * amount of time. During this operation, this repository can be queried, but
   * there is no guarantee of consistency when querying the entries that are in process
   * of removal.
   */
  void deleteEntriesOlderThan(Timestamp oldestDate) {
    Query<Entity> query = Query.newEntityQueryBuilder()
                               .setKind("revision")
                               .setFilter(PropertyFilter.lt("timestamp", oldestDate)).build();

    Iterable<Entity> results = storage.query(query, Entity.class).getIterable();

    for (Entity entity : results) {
      storage.delete(entity);
    }
  }

  /**
   * A subroutine created for running at fixed intervals, delegating {@code deleteEntriesOlderTHan}
   * for the deletion procedure. Scheduled for running every Sunday at 00:00 server time.
   */
  @Scheduled(cron = "0 0 * * 0")
  private final Runnable cleanup = new Runnable() {
    public void run() {
      deleteEntriesOlderThan(Timestamp.of(getEarliestAliveTime()));
    }
  };

  /**
   * This method provides a stateless way to retrieve the timestamp before which to
   * delete all older entries.
   *
   * @return Date object with the appropriate "delete before" timestamp.
   */
  private static Date getEarliestAliveTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DAY_OF_MONTH, -ttl);
    return calendar.getTime();
  }
}
