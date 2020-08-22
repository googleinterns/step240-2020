package com.google.graphgeckos.dashboard.api.components;

public class Log {
  private final String type;
  private final String link;

  public Log(String type, String link) {
    this.type = type;
    this.link = link;
  }

  public String getType() {
    return type;
  }

  public String getLink() {
    return link;
  }
  
}
