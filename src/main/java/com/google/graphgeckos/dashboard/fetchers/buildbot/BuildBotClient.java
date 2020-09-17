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

/** A (external) BuildBot API json data fetcher. */
public class BuildBotClient {

  /** Provides access to the storage. */
  @Autowired
  private DatastoreRepository datastoreRepository;

  /** Base url of the BuildBot API. */
  private final String baseUrl;

  public BuildBotClient(@NonNull String baseUrl) {
    Objects.requireNonNull(baseUrl);
    this.baseUrl = baseUrl;
  }

  /**
   * Starts fetching process. Fetches data every {@code delay} seconds from the url:
   * {@code baseUrl}/{@code buildBot}/builds/{@code buildId}?as_text=1.
   * E.g with buildBot = "clang-x86_64-debian-fast" and buildId = 1000,
   * baseUrl = "http://lab.llvm.org:8011/json/builders" the request url will be
   * http://lab.llvm.org:8011/json/builders/clang-x86_64-debian-fast/builds/1000?as_text=1 .
   * Adds valid fetched data in the form of {@link BuildBotData} to the storage and tries
   * to fetch an entry with the next buildId after waiting for {@code delay} seconds.
   * If the fetched data is empty (invalid) waits for {@code delay} seconds and tries
   * to make the same request.
   *
   * @param buildBot name of the BuildBot as it is in the API (e.g "clang-x86_64-debian-fast").
   * @param initialBuildId the id of the BuildBot's build from where to start fetching data.
   */
  public void run(@NonNull String buildBot, long initialBuildId, long delay) {
    Objects.requireNonNull(buildBot);

    AtomicLong buildId = new AtomicLong(initialBuildId);

    WebClient.builder().baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .build()
      .get()
      .uri(String.format( "/%s/builds/%d?as_text=1", buildBot, buildId.get()))
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
