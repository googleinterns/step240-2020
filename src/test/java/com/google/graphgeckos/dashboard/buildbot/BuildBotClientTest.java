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

package com.google.graphgeckos.dashboard.buildbot;

import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.Log;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class BuildBotClientTest {

  private MockWebServer mockWebServer = new MockWebServer();

  /** Tested BuildBot API fetcher. */
  @InjectMocks BuildBotClient client = new BuildBotClient("ddd");

  @Mock DatastoreRepository datastoreRepository;

  private final String VALID_BUILD_BOT_NAME_CLANG = "clang-x86_64-debian-fast";
  private final String VALID_BUILD_BOT_NAME_FUCHSIA = "fuchsia-x86_64-linux";
  private final String NOT_FOUND_BUILD_BOT_NAME = "server-will-respond-with-404";
  private final String EMPTY_JSON_BUILD_BOT_NAME = "server-will-respond-with-empty-json";

  private final long INITIAL_BUILD_ID = 36624;
  private final long NEXT_BUILD_ID = INITIAL_BUILD_ID + 1;
  private BuildBotClientTestInfo BUILD_BOT_CLANG =
      new BuildBotClientTestInfo(VALID_BUILD_BOT_NAME_CLANG);
  private BuildBotClientTestInfo BUILD_BOT_FUCHSIA =
      new BuildBotClientTestInfo(VALID_BUILD_BOT_NAME_FUCHSIA);

  private final String EMPTY_JSON = "";

  /** Request frequency. */
  private final long DELAY_ONE_SECOND = 1;

  /** Returns the contents of the test json file in src/test/resources/jsons/buildbots. */
  private final String getTestJson(String filename) {
    try {
      final String path = "src/test/resources/jsons/buildbots/" + filename;
      return Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Determines the behaviour of the mocked server depending on the provided name of the Build Bot.
   */
  Dispatcher dispatcher =
      new Dispatcher() {

        /**
         * Response when the latest builds are requested., e.g., for:
         * /builders/fuchsia-x86_64-linux/builds?order=-buildid&limit=20
         */
        private final MockResponse LATEST_BUILDS_VALID_RESPONSE =
            new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getTestJson("latest-builds.json"));

        /** Response when the build changes builds are requested., e.g., for: /builds/1/changes */
        private final MockResponse CHANGES_VALID_RESPONSE =
            new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getTestJson("builds-1-changes.json"));

        /**
         * Response when the {@code VALID_BUILD_BOT_NAME_FUCHSIA} data is requested. Status OK
         * (200), Responds with the original (taken from the API) fuchsia-x86_64-linux JSON.
         */
        private final MockResponse FUCHSIA_VALID_RESPONSE =
            new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(BUILD_BOT_FUCHSIA.getContent());

        /**
         * Response when the {@code VALID_BUILD_BOT_NAME_FUCHSIA} data is requested. Status NOT
         * FOUND (404).
         */
        private final MockResponse PAGE_NOT_FOUND_RESPONSE =
            new MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        /**
         * Response when the {@code VALID_BUILD_BOT_NAME_FUCHSIA} data is requested. Status OK
         * (200). Responds with empty JSON.
         */
        private final MockResponse EMPTY_JSON_RESPONSE =
            new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(EMPTY_JSON);

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

          final String path = request.getPath();
          if (path.startsWith("/builders/")) {
            if (path.contains(VALID_BUILD_BOT_NAME_CLANG)
                || path.contains(VALID_BUILD_BOT_NAME_FUCHSIA)) {
              return LATEST_BUILDS_VALID_RESPONSE;
            }
            return PAGE_NOT_FOUND_RESPONSE;
          } else if (path.startsWith("/builds/") && path.contains("/changes")) {
            return CHANGES_VALID_RESPONSE;
          }

          if (request.getPath().contains(VALID_BUILD_BOT_NAME_CLANG)) {
            if (request.getPath().contains(Long.toString(INITIAL_BUILD_ID))) {
              return LATEST_BUILDS_VALID_RESPONSE;
            }
            return FUCHSIA_VALID_RESPONSE;
          }

          if (request.getPath().contains(NOT_FOUND_BUILD_BOT_NAME)) {
            return PAGE_NOT_FOUND_RESPONSE;
          }

          if (request.getPath().contains(EMPTY_JSON_BUILD_BOT_NAME)) {
            return EMPTY_JSON_RESPONSE;
          }

          /*
          Handles requests to the defined Build Bots only to verify that
          the client performs requests of the expected form.
          */
          throw new IllegalStateException("Unknown build bot name: " + request.getPath());
        }
      };

  /**
   * Compares objects, stubbed from the {@link DatastoreRepository::updateRevisionEntry}, with the
   * corresponding expected values.
   */
  static class BuildBotDataMatcher implements ArgumentMatcher<BuildBotData> {

    /**
     * Expected output fields. See {@link com.google.graphgeckos.dashboard.datatypes.BuildBotData}
     * to learn more.
     */
    private String commitHash;

    private Timestamp timestamp;
    private String name;
    private BuilderStatus status;
    private List<Log> logs;

    public BuildBotDataMatcher(BuildBotClientTestInfo expected) {
      this.commitHash = expected.getCommitHash();
      this.timestamp = expected.getTimestamp();
      this.name = expected.getName();
      this.status = expected.getStatus();
      this.logs = expected.getLogs();
    }

    /**
     * Checks if an instance of the {@link BuildBotData} provided by the tested {@code client} as an
     * argument when calling {@link DatastoreRepository::updateRevisionEntry}.
     *
     * @param data instance of the {@link BuildBotData} to inspect
     * @return true if the {@code data} is valid {@see BuildBotData::isValid}.
     */
    @Override
    public boolean matches(BuildBotData data) {
      return data.getCommitHash().equals(commitHash)
          && data.getStatus().equals(status)
          && data.getName().equals(name)
          && data.getTimestamp().equals(timestamp)
          && data.getLogs().equals(logs);
    }
  }

  /** Enables Mockito. */
  @Before
  public void init() throws IOException {
    MockitoAnnotations.initMocks(this);

    mockWebServer.start();
    mockWebServer.setDispatcher(dispatcher);
    String baseUrl = "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort();
    client.setBaseUrl(baseUrl);
  }

  @After
  public void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  private long secondsToMillis(long seconds) {
    return seconds * 1000;
  }

  /**
   * Should add fetched data in the form of {@link BuildBotData} POJO to the storage via an instance
   * of {@link DatastoreRepository}.
   *
   * <p>Given: valid build bot name, build id and delay. Expected behaviour: fetches JSON, turns
   * JSON into a POJO, passes this object as an argument to {@link
   * DatastoreRepository::updateEntry}.
   */
  @Test
  public void validResponseCausesUpdateCallToRepositoryWithValidArgument() {
    client.run(VALID_BUILD_BOT_NAME_CLANG);

    // Wait to check if the datastoreRepository::updateRevisionEntry was called
    // because it takes time to get a response from server.
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).atLeast(1))
        .updateRevisionEntry(Mockito.any());
  }

  /**
   * Shouldn't attempt to access the storage via an instance of {@link DatastoreRepository} or throw
   * Exception when server responds with 404 exception.
   */
  @Test
  public void serverErrorDoesNotCauseCallToRepository() throws Exception {
    client.run(NOT_FOUND_BUILD_BOT_NAME);
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).never())
        .updateRevisionEntry(Mockito.any());
  }

  /**
   * Shouldn't attempt to access the storage via an instance of {@link DatastoreRepository} or throw
   * exception when server responds with empty JSON.
   */
  @Test
  public void emptyJsonResponseDoesNotCauseCallToRepository() throws Exception {
    client.run(EMPTY_JSON_BUILD_BOT_NAME);
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).never())
        .updateRevisionEntry(Mockito.any());
  }

  /** Should access the storage at least twice when given enough time for more than one request. */
  @Test
  public void twoValidResponsesCauseTwoCallsToRepository() throws Exception {
    client.run(VALID_BUILD_BOT_NAME_CLANG);
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 5;
    Mockito.verify(datastoreRepository, Mockito.timeout(delay).atLeast(2))
        .updateRevisionEntry(Mockito.any());
  }
}
