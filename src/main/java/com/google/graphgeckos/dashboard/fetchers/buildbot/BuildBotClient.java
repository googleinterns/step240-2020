package com.google.graphgeckos.dashboard.fetchers.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildBotClient {

  /** Provides access to the storage. */
  @Autowired
  private DatastoreRepository datastoreRepository;

  private final String baseUrl;

  private final int requestFrequencyInSeconds;

  public BuildBotClient(@NonNull String baseUrl, int requestFrequencyInSeconds) {
    Objects.requireNonNull(baseUrl);
    this.baseUrl = baseUrl;
    this.requestFrequencyInSeconds = requestFrequencyInSeconds;
  }

  public int getRequestFrequency() {
    return requestFrequencyInSeconds;
  }
  
  public void run(@NonNull String buildBot, int initialBuildId) {
    Objects.requireNonNull(buildBot);

    AtomicInteger buildId = new AtomicInteger(initialBuildId);

    WebClient.builder().baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .build()
      .get()
      .uri(String.format( "%s/builds/%d?as_text=1", buildBot, buildId.get()))
      .accept(MediaType.TEXT_PLAIN)
      .retrieve()
      .bodyToMono(String.class)
      .delaySubscription(Duration.ofSeconds(requestFrequencyInSeconds))
      .onErrorReturn("error")
      .repeat()
      .subscribe(response -> {
        if (response.equals("error")) {
          System.out.println("error");
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