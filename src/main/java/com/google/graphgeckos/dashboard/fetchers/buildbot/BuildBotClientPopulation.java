package com.google.graphgeckos.dashboard.fetchers.buildbot;

import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.Arrays;

public class BuildBotClientPopulation {

  private BuildBotClientPopulation() {}

  public static class BuildBotClientInitializer {

    @Autowired
    private DatastoreRepository datastoreRepository;

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
    Arrays.stream(buildBots).forEach(x -> BuildBotClient.run(x.name, x.initialBuildId));
  }

}