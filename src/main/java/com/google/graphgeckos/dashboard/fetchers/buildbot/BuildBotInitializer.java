package com.google.graphgeckos.dashboard.fetchers.buildbot;

public class BuildBotInitializer {
  public final String name;
  public final int initialBuildId;

  public BuildBotInitializer(String name, int initialBuildId) {
    this.name = name;
    this.initialBuildId = initialBuildId;
  }

}