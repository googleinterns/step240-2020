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

package com.google.graphgeckos.dashboard.github;

import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import com.google.graphgeckos.dashboard.datatypes.BuilderStatus;
import com.google.graphgeckos.dashboard.datatypes.Log;
import com.google.graphgeckos.dashboard.fetchers.github.GitHubClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
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
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GitHubClientTest {

  private MockWebServer mockWebServer = new MockWebServer();

  /**
   * Tested BuildBot API fetcher.
   */
  @InjectMocks
  GitHubClient client = new GitHubClient();

  @Mock
  DatastoreRepository datastoreRepository;

  private final String VALID_MASTER = "llvm_master_json";

  private GitHubJsonTestInfo LLVM_MASTER = new GitHubJsonTestInfo(VALID_MASTER);

  private final String EMPTY_JSON = "";

  /**
   * Request frequency.
   */
  private final long DELAY_ONE_SECOND = 1;

  /**
   * Determines the behaviour of the mocked server depending on the
   * provided name of the Build Bot.
   */
  Dispatcher dispatcher = new Dispatcher() {

    /**
     * Response when the {@code VALID_MASTER} data is requested. Status OK(200),
     * Responds with the original (taken from the API) clang-x86_64-debian-fast JSON.
     */
    private final MockResponse LLVM_MASTER_RESPONSE = new MockResponse()
      .setResponseCode(200)
      .setHeader(
        HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_JSON_VALUE)
      .setBody(LLVM_MASTER.getContent());

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

      /* Expected form of the request url: baseUrl/buildBot/builds/buildId?as_text=1 . */
      if (request.getPath().contains(VALID_MASTER)) {
          return LLVM_MASTER_RESPONSE;
      }

      /*
      Handles requests to the defined Build Bots only to verify that
      the client performs requests of the expected form.
      */
      throw new IllegalStateException("Unknown Github Link");
    }

  };

  /**
   * Compares objects, stubbed from the {@link DatastoreRepository::createRevisionEntry},
   * with the corresponding expected values.
   */
  static class GitHubDataMatcher implements ArgumentMatcher<GitHubData> {

    /**
     * Expected output fields. See {@link com.google.graphgeckos.dashboard.datatypes.GitHubData} to learn more.
     */
    private String branch;
    private String commitHash;
    private String timestamp;
    private String repositoryLink;

    public GitHubDataMatcher(GitHubJsonTestInfo expected) {
      this.commitHash = expected.getCommitHash();
      this.timestamp = expected.getTimestamp();
      this.branch = expected.getBranch();
      this.repositoryLink = expected.getRepositoryLink();
    }

    /**
     * Checks if an instance of the {@link GitHubData} provided by the tested {@code client}
     * as an argument when calling {@link DatastoreRepository::createRevisionEntry}.
     *
     * @param data instance of the {@link GitHubData} to inspect
     * @return true if the {@code data} is valid {@see GitHubData::isValid}.
     */
    @Override
    public boolean matches(GitHubData data) {
      return data.getCommitHash().equals(commitHash)
        && data.getTimestamp().equals(timestamp)
        && data.getBranch().equals(branch)
        && data.getRepositoryLink().equals(repositoryLink);
    }

  }

  /**
   * Enables Mockito.
   */
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
   * Should add fetched data in the form of {@link GitHubData} POJO
   * to the storage via an instance of {@link DatastoreRepository}.
   * <p>
   * Given: valid build bot name, build id and delay.
   * Expected behaviour: fetches JSON, turns JSON into a POJO,
   * passes this object as an argument to {@link DatastoreRepository::updateEntry}.
   */
  @Test
  public void validResponseCausesUpdateCallToRepositoryWithValidArgument() {
    client.run(DELAY_ONE_SECOND);

    // Wait to check if the datastoreRepository::createRevisionEntry was called
    // because it takes time to get a response from server.
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).atLeast(1))
      .createRevisionEntry(
        Mockito.argThat(new GitHubDataMatcher(LLVM_MASTER)));
  }

  /**
   * Shouldn't attempt to access the storage via an instance of {@link DatastoreRepository}
   * or throw Exception when server responds with 404 exception.
   */
  @Test
  public void serverErrorDoesNotCauseToRepository() throws Exception {
    client.run(DELAY_ONE_SECOND);
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).never()).createRevisionEntry(Mockito.any());
  }

  /**
   * Shouldn't attempt to access the storage via an instance of {@link DatastoreRepository}
   * or throw exception when server responds with empty JSON.
   */
  @Test
  public void emptyJsonResponseDoesNotCauseCallToRepository() throws Exception {
    client.run(DELAY_ONE_SECOND);
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(delay).never()).createRevisionEntry(Mockito.any());
  }

  /**
   * Should access the storage at least twice when given enough time for more than one request.
   */
  @Test
  public void twoValidResponsesCauseTwoCallsToRepository() throws Exception {
    client.run(DELAY_ONE_SECOND);
    long delay = secondsToMillis(DELAY_ONE_SECOND) * 5;
    Mockito.verify(datastoreRepository, Mockito.timeout(delay).atLeast(2)).createRevisionEntry(Mockito.any());
  }

}
