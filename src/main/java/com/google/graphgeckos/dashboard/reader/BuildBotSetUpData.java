package com.google.graphgeckos.dashboard.reader;

public class BuildBotSetUpData {

  // Name of the BuildBot
  public final String name;
  
  // Build id from where to start fetching.
  public final long initialId;

  // Request frequency in seconds.
  public final long delay;

  public BuildBotSetUpData(String name, long initialId, long delay) {
    this.name = name;
    this.initialId = initialId;
    this.delay = delay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BuildBotSetUpData)) {
      return false;
    }
    BuildBotSetUpData toCompare = (BuildBotSetUpData) o;
    return name.equals(toCompare.name) && delay == toCompare.delay && initialId == toCompare.initialId;
  }

}
