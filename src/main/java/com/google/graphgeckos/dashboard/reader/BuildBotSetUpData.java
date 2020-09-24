package com.google.graphgeckos.dashboard.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildBotSetUpData {

  @JsonProperty("name")
  public final String name;

  @JsonProperty("initialId")
  public final long initialId;

  @JsonProperty("delay_in_seconds")
  public final long delay;

  public BuildBotSetUpData(String name, long initialId, long delay) {
    this.name = name;
    this.initialId = initialId;
    this.delay = delay;
  }

}
