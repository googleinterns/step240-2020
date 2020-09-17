package com.google.graphgeckos.dashboard.fetchers.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class BuildBotClient {

  /** Provides access to the storage. */
  @Autowired
  private DatastoreRepository datastoreRepository;

  /** Base URL of the BuildBot API. */
  private final String baseUrl;

  /** Fetcher does requests to the BuildBot API every {@code delay} seconds. */
  private final long delay;

  public BuildBotClient(@NonNull String baseUrl, long delay) {
    Objects.requireNonNull(baseUrl);
    this.baseUrl = baseUrl;
    this.delay = delay;
  }

  /** Returns request frequency in seconds. */
  public long getDelay() {
    return delay;
  }

  /**
   * @param buildBot name of the BuildBot as it is in the API (e.g "clang-x86_64-debian-fast").
   * @param initialBuildId
   */
  public void run(@NonNull String buildBot, long initialBuildId) {
    Objects.requireNonNull(buildBot);

    AtomicLong buildId = new AtomicLong(initialBuildId);

    WebClient.builder().baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .build()
      .get()
      .uri(String.format( "%s/builds/%d?as_text=1", buildBot, buildId.get()))
      .accept(MediaType.TEXT_PLAIN)
      .retrieve()
      .bodyToMono(String.class)
      .delaySubscription(Duration.ofSeconds(delay))
      .onErrorResume(e -> {
        System.out.println("Ignoring error: " + e.getMessage());
        return Mono.empty();
      })
      .repeat()
      .subscribe(response -> {
        if (response.isEmpty()) {
          return;
        }
        try {
          BuildBotData builder = new ObjectMapper().readValue(response, BuildBotData.class);
          datastoreRepository.updateRevisionEntry(builder);
        } catch (Exception e) {
          e.printStackTrace();
        }
        buildId.incrementAndGet();
      });
  }

}