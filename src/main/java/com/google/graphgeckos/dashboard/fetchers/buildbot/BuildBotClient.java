package com.google.graphgeckos.dashboard.fetchers.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.graphgeckos.dashboard.storage.DataRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import javax.annotation.Resource;
import java.time.Duration;

public class BuildBotClient {
  private static final String BASE_URL = "http://lab.llvm.org:8011/json/builders/";

  @Resource(name = "dataRepository")
  // TODO: change to private static DataRepository dataRepository = new DataRepositoryImplementation();
  private DataRepository dataRepository;
  private long buildId;

  public BuildBotClient(@NonNull String buildBot, int initialBuildId) {
    buildId = initialBuildId;

    WebClient.builder().baseUrl(BASE_URL)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .build()
      .get()
      .uri(String.format( "%s/builds/%d?as_text=1", buildBot, buildId))
      .accept(MediaType.TEXT_PLAIN)
      .retrieve()
      .bodyToMono(String.class)
      .delaySubscription(Duration.ofSeconds(2))
      .onErrorReturn("error")
      .repeat()
      .subscribe(response -> {
        if (response.equals("error")) {
          return;
        }

        try {
          System.out.println(new ObjectMapper().readValue(response, BuildBotData.class));
          BuildBotData builder = new ObjectMapper().readValue(response, BuildBotData.class);
          System.out.println(builder);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
        buildId++;
      });
    // TODO: [issue #68] Implement interaction between BuildBotController and storage
  }

}