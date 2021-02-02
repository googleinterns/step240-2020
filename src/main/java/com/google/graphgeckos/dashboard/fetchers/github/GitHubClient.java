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

import com.google.api.client.util.Preconditions;
import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import java.time.Duration;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient {

  /** Provides access to the storage. */
  private DatastoreRepository datastoreRepository;

  /**
   * The GitHub API URL of the commits of the repository, e.g.:
   * https://api.github.com/repos/llvm/llvm-project/commits
   */
  private String url;

  private static final Logger logger = Logger.getLogger(GitHubClient.class.getName());

  public GitHubClient(@NonNull String baseUrl, @NonNull DatastoreRepository repository) {
    this.url = Preconditions.checkNotNull(baseUrl);
    this.datastoreRepository = Preconditions.checkNotNull(repository);
  }

  public void run(@NonNull long delay) {

    logger.info(String.format("GitHub: started fetching from the base url: %s", url));
    WebClient.builder()
        .baseUrl(url)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .build()
        .get()
        .uri(url)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(GitHubData[].class)
        .delaySubscription(Duration.ofSeconds(delay))
        .onErrorResume(
            e -> {
              logger.info("Ignoring error: " + e.getMessage());
              return Mono.empty();
            })
        .repeat()
        .subscribe(
            responses -> {
              if (responses == null) {
                logger.info(String.format("GitHub: Error occurred, waiting for %d seconds", delay));
                return;
              }
              for (GitHubData response : responses) {
                datastoreRepository.createRevisionEntry(response);
              }
              logger.info(String.format("GitHub: Performing re-request in %d seconds", delay));
            });
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
