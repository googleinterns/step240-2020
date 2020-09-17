package com.google.graphgeckos.dashboard.fetchers.buildbot;

public class BuildBotInitializer {
  public final String name;
  public final long initialBuildId;

  public BuildBotInitializer(String name, long initialBuildId) {
    this.name = name;
    this.initialBuildId = initialBuildId;
  }

}