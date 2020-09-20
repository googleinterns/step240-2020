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

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class BuildBotClientTest {

  static class BuildBotDataMatcher implements ArgumentMatcher<BuildBotData> {

    /**
     * Checks if an instance of the {@link BuildBotData} provided by the tested {@code client}
     * as an argument when calling {@link DatastoreRepository::updateRevisionEntry}.
     *
     * @param data instance of the {@link BuildBotData} to inspect.
     * @return true if the {@code data} is valid {@see BuildBotData::isValid}.
     */
    @Override
    public boolean matches(BuildBotData data) {
      return data.getCommitHash() != null
        && data.getName() != null
        && data.getStatus() != null
        && data.getLogs() != null;
    }
  }

  private MockWebServer server = new MockWebServer();

  /**
   * Tested BuildBot API fetcher.
   */
  @InjectMocks
  BuildBotClient client = new BuildBotClient("ddd");

  @Mock
  DatastoreRepository datastoreRepository;

  private final String VALID_BUILD_BOT_NAME = "clang-x86_64-debian-fast";
  private final String NOT_FOUND_BUILD_BOT_NAME = "server-will-respond-with-404";
  private final String EMPTY_JSON_BUILD_BOT_NAME = "server-will-respond-with-empty-json";

  private final long INITIAL_BUILD_ID = 36624;
  private final long NEXT_BUILD_ID = INITIAL_BUILD_ID + 1;
  private BuildBotClientTestInfo BUILD_BOT = new BuildBotClientTestInfo(VALID_BUILD_BOT_NAME);
  private final String EMPTY_JSON = "";

  /**
   * Request frequency.
   */
  private final long DELAY_ONE_SECOND = 1;

  Dispatcher dispatcher = new Dispatcher() {
    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
      MockResponse firstValidResponse = new MockResponse()
                                            .setResponseCode(200)
                                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                            .setBody(BUILD_BOT.getContent());
      MockResponse pageNotFoundResponse = new MockResponse()
                                          .setResponseCode(404)
                                          .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      MockResponse emptyJsonResponse = new MockResponse()
                                           .setResponseCode(200)
                                           .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                           .setBody(EMPTY_JSON);

      if (request.getPath().contains(VALID_BUILD_BOT_NAME)) {
        if (request.getPath().contains(Long.toString(INITIAL_BUILD_ID))) {
          return firstValidResponse;
        }
        return firstValidResponse;
      }

      if (request.getPath().contains(NOT_FOUND_BUILD_BOT_NAME)) {
        return pageNotFoundResponse;
      }

      if (request.getPath().contains(EMPTY_JSON_BUILD_BOT_NAME)) {
        return emptyJsonResponse;
      }

      throw new IllegalStateException();
    }
  };

  /**
   * Enables Mockito.
   */
  @Before
  public void init() throws IOException {
    MockitoAnnotations.initMocks(this);
    server.play();
    String baseUrl = server.getUrl("").toString();
    client.setBaseUrl(baseUrl);
    server.setDispatcher(dispatcher);
  }

  @After
  public void tearDown() throws IOException {
    server.shutdown();
  }

  private long secondsToMillis(long seconds) {
    return seconds * 1000;
  }

  /**
   * Should add fetched data in the form of {@link BuildBotData} POJO
   * to the storage via an instance of {@link DatastoreRepository}.
   * <p>
   * Given: valid build bot name, build id and delay.
   * Expected behaviour: fetches JSON, turns JSON into a valid (no null fields) POJO,
   * passes this object as an argument to {@link DatastoreRepository::updateEntry}.
   */
  @Test
  public void validResponseCausesUpdateCallToRepositoryWithValidArguments() {

    client.run(VALID_BUILD_BOT_NAME, INITIAL_BUILD_ID, DELAY_ONE_SECOND);

    // Wait to check if the datastoreRepository::updateRevisionEntry was called.
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).atLeast(1))
      .updateRevisionEntry(Mockito.argThat(new BuildBotDataMatcher()));
  }

  /**
   * Shouldn't attempt to access the storage via an instance of {@link DatastoreRepository}.
   * <p>
   * Given: invalid arguments (build id).
   * Expected behaviour: doesn't access storage or throw any Exception.
   */
  @Test
  public void serverErrorCausesNoCallToRepository() throws Exception {

    client.run(NOT_FOUND_BUILD_BOT_NAME, INITIAL_BUILD_ID, DELAY_ONE_SECOND);

    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).never()).updateRevisionEntry(Mockito.any());
  }

  @Test
  public void emptyJsonResponseCausesNoCallToRepository() throws Exception {

    client.run(EMPTY_JSON_BUILD_BOT_NAME, INITIAL_BUILD_ID, DELAY_ONE_SECOND);

    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).never()).updateRevisionEntry(Mockito.any());
  }

  @Test
  public void twoValidResponsesCausesTwoCallsToRepository() throws Exception {
    client.run(VALID_BUILD_BOT_NAME, INITIAL_BUILD_ID, DELAY_ONE_SECOND);

    long delay = secondsToMillis(DELAY_ONE_SECOND) * 10;
    Mockito.verify(datastoreRepository, Mockito.timeout(delay).atLeast(2)).updateRevisionEntry(Mockito.any());
  }

}
