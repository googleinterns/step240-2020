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
  private static DatastoreRepository datastoreRepository;

  private static String baseUrl = "http://lab.llvm.org:8011/json/builders/";

  private static int requestFrequency = 10;

  public static void setBaseUrl(@NonNull String baseUrl) {
    Objects.requireNonNull(baseUrl);
    BuildBotClient.baseUrl = baseUrl;
  }

  public static void setRequestFrequency(int requestFrequency) {
    BuildBotClient.requestFrequency = requestFrequency;
  }

  public static int getRequestFrequency() {
    return requestFrequency;
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
      .delaySubscription(Duration.ofSeconds(requestFrequency))
      .onErrorReturn("error")
      .repeat()
      .subscribe(response -> {
        if (response.equals("error")) {
          System.out.println("error");
          return;
        }
        System.out.println(response);
        try {
          BuildBotData builder = new ObjectMapper().readValue(response, BuildBotData.class);
          System.out.println(builder.getCommitHash());
          datastoreRepository.updateRevisionEntry(builder);
        } catch (Exception e) {
          System.out.println("SUKA BLIAT'");
          e.printStackTrace();
        }
        buildId.incrementAndGet();
      });
  }

}