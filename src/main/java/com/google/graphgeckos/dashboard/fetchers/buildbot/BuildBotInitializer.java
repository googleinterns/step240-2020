package com.google.graphgeckos.dashboard.fetchers.buildbot;

import org.springframework.lang.NonNull;
import java.util.Objects;

/** Representation of the BuildBot. */
public class BuildBotInitializer {

  /** Name of the BuildBot as it is in the API. */
  public final String name;

  /** Number of the build to start fetching from. */
  public final long initialBuildId;

  /** Request frequency in seconds. */
  public final long delay;

  public BuildBotInitializer(@NonNull String name, long initialBuildId, long delay) {
    Objects.requireNonNull(name);

    this.name = name;
    this.initialBuildId = initialBuildId;
    this.delay = delay;
  }

}