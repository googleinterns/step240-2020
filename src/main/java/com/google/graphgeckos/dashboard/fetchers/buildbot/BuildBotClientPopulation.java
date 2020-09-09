package com.google.graphgeckos.dashboard.fetchers.buildbot;

import org.springframework.lang.NonNull;

import java.util.Arrays;

public abstract class BuildBotClientPopulation {

  public static class BuildBotClientInitializer {
    public final String name;
    public final int initialBuildId;

    public BuildBotClientInitializer(String name, int initialBuildId) {
      this.name = name;
      this.initialBuildId = initialBuildId;
    }

  }

  public static void populate(@NonNull BuildBotClientInitializer[] buildBots) {
    if (buildBots.length == 0) {
      throw new IllegalArgumentException("Expected one or more BuildBots, found zero");
    }
    Arrays.stream(buildBots).forEach(x -> new BuildBotClient(x.name, x.initialBuildId));
  }

}