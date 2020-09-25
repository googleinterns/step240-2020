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

package com.google.graphgeckos.dashboard.fetchers.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Preconditions;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.logging.Logger;

public class GitHubClient {

  /** Provides access to the storage. */
  @Autowired
  private DatastoreRepository datastoreRepository;

  private String baseUrl;

  private static final Logger logger = Logger.getLogger(GitHubClient.class.getName());

  private GitHubClient(@NonNull String baseUrl) {
    this.baseUrl = Preconditions.checkNotNull(baseUrl);
  }

  public GitHubClient() {
    this("https://api.github.com/repos/llvm/llvm-project/commits/master");
  }

  public void run(@NonNull long delay) {

    logger.info(String.format("GitHub: started fetching from the base url: %s",
                              baseUrl));     
    WebClient.builder().baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .build()
      .get()
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToFlux(GitHubData.class)
      .delaySubscription(Duration.ofSeconds(delay))
      .onErrorResume(e -> {
        logger.info("Ignoring error: " + e.getMessage());
        return Mono.empty();
      })
      .repeat()
      .subscribe(response -> {
        if (response == null) {
          logger.info(
            String.format("GitHub: Error occurred, waiting for %d seconds", delay));
          return;
        }

        logger.info(String.format("GitHub: trying to deserialize valid JSON"));
        try {
          //GitHubData gitHubData = new ObjectMapper().readValue(response, GitHubData.class);
          datastoreRepository.createRevisionEntry(response);
        } catch (Exception e) {
          logger.info(String.format("GitHub: can't deserialize JSON"));
          e.printStackTrace();
        }
        logger.info(
               String.format(
                 "GitHub: Performing re-request in %d seconds", delay));
      });
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

}
