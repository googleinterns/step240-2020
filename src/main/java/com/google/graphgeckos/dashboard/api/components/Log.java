package com.google.graphgeckos.dashboard.api.components;

/**
 * Represents a log of a buildbot.
 */
public class Log {
  // log type
  private final String type;
  // full log link
  private final String link;

  public Log(String type, String link) {
    this.type = type;
    this.link = link;
  }

  /**
   * @return a type of the log
   */
  public String getType() {
    return type;
  }

  /**
   * @return a link to the full version of the log
   */
  public String getLink() {
    return link;
  }

}
