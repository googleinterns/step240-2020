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

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.List;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;

/* Useful links:
https://googleapis.dev/java/google-cloud-datastore/latest/index.html
https://googleapis.dev/java/spring-cloud-gcp/1.2.2.RELEASE/index.html
https://cloud.google.com/datastore/docs/concepts/
https://spring.io/projects/spring-cloud-gcp#overview
*/

/**
 * Implementation of the DataRepository, with the purpose of providing I/O to
 * the Google Appengine Datastore component. Each individual database entry is
 * structured as follows:
 *    - Kind: "revision"
 *    - Key: commit hash
 *    - Properties:
 *      * timestamp
 *      * branch
 *      * individual commit information (Entities)
 */
@SpringBootApplication
public class GCDataRepository implements DataRepository {
  private Calendar calendar = Calendar.getInstance();
  private DatastoreTemplate storage;
  private final ScheduledExecutorService cleanScheduler = Executors.newScheduledThreadPool(1);
  private final ScheduledFuture<?> cleanerProcessHandle;

  public GCDataRepository() {
    calendar.setTime(new Date()); 

    // these parameters will have to change before deployment
    long initialDelay = 1;
    long period = 1;
    TimeUnit periodUnit = TimeUnit.MINUTES;

    cleanerProcessHandle = cleanScheduler.scheduleAtFixedRate(cleanup, initialDelay, period, periodUnit);
  }

  public void createRevisionEntry(ParsedGitData entryData) throws IllegalArgumentException {
    if (entryData == null) {
      throw new IllegalArgumentException("entryData cannot be null");
    } else if (!entryData.validCreateData()) {
      throw new IllegalArgumentException("entryData does not contain all necessary fields");
    }

    if (getRevisionEntry(entryData.getCommitHash()) == null) {
      storage.save(new BuildInfo(entryData););
    }
  }

  public void updateRevisionEntry(ParsedBuildbotData updateData) throws IllegalArgumentException {
    if (updateData == null) {
      throw new IllegalArgumentException("entryData cannot be null");
    } else if (updateData.validUpdateData()) {
      throw new IllegalArgumentException("updateData does not contain all necessary fields");
    }

    BuildInfo associatedEntity = getRevisionEntry(updateData.getCommitHash());

    if (associatedEntity != null) {
      associatedEntity.addBuilder(updateData.toBuilder());

      storage.save(associatedEntity);
    }
  }

  public void deleteRevisionEntry(String commitHash) throws IllegalArgumentException {
    if (commitHash == null) {
      throw new IllegalArgumentException("commitHash cannot be null");
    }

    BuildInfo toBeDeleted = getRevisionEntry(commitHash);

    if (toBeDeleted != null) {
      storage.delete(toBeDeleted);
    }
  }

  public Iterable<BuildInfo> getLastRevisionEntries(int number, int offset)
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

  private BuildInfo getRevisionEntry(String commitHash) {
    return storage.findById(commitHash, BuildInfo.class);
  }

  private final Runnable cleanup = new Runnable() {
    public void run() {
      calendar.add(Calendar.MONTH, -3);

      TimestampValue borderDate = TimestampValue.of(Timestamp.of(calendar.getTime()));

      Query<Entity> query = Query.newEntityQueryBuilder()
                                 .setKind("revision")
                                 .setFilter(PropertyFilter.lt("timestamp", borderDate)).build();

      Iterable<Entity> results = storage.query(query, Entity.class).getIterable();

      for (Entity entity : results) {
        storage.delete(entity);
      }
    }
  };
}
