package com.google.graphgeckos.dashboard.fetchers.buildbot;

import org.springframework.lang.NonNull;
import java.util.Arrays;
import java.util.Objects;

/** BuildBot fetchers creator. Create a population of different fetchers with common baseUrl. */
public class BuildBotClientPopulation {

  /** Base url of the BuildBot API. */
  private final String baseUrl;

  public BuildBotClientPopulation(@NonNull String baseUrl) {
    Objects.requireNonNull(baseUrl);

    this.baseUrl = baseUrl;
  }

  public BuildBotClientPopulation() {
    this("http://lab.llvm.org:8011/json/builders/");
  }

  /**
   * Creates and runs fetchers to fetch data from the BuildBots with the given name and initial build id,
   * provided in the form of {@link BuildBotInitializer}.
   *
   * @param buildBots information about the BuildBots to fetch data from.
   */
  public void populate(@NonNull BuildBotInitializer... buildBots) {
    Objects.requireNonNull(buildBots);

    if (buildBots.length == 0) {
      throw new IllegalArgumentException("Expected one or more BuildBots, found zero");
    }
    Arrays.stream(buildBots)
          .forEach(x -> new BuildBotClient(baseUrl)
          .run(x.name, x.initialBuildId, x.delay));
  }

}
