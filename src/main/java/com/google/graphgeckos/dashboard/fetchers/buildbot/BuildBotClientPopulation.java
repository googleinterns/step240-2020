package com.google.graphgeckos.dashboard.fetchers.buildbot;

import org.springframework.lang.NonNull;
import java.util.Arrays;
import java.util.Objects;

public class BuildBotClientPopulation {

  private final String baseUrl;
  private final int requetFrequencyInSeconds;

  public BuildBotClientPopulation(@NonNull String baseUrl, int requestFrequencyInSeconds) {
    Objects.requireNonNull(baseUrl);

    this.baseUrl = baseUrl;
    this.requetFrequencyInSeconds = requestFrequencyInSeconds;
  }

  public BuildBotClientPopulation() {
    this("http://lab.llvm.org:8011/json/builders/", 10);
  }

  public void populate(@NonNull BuildBotInitializer... buildBots) {
    Objects.requireNonNull(buildBots);

    if (buildBots.length == 0) {
      throw new IllegalArgumentException("Expected one or more BuildBots, found zero");
    }
    Arrays.stream(buildBots)
      .forEach(x -> new BuildBotClient(baseUrl, requetFrequencyInSeconds).run(x.name, x.initialBuildId));
  }

}
