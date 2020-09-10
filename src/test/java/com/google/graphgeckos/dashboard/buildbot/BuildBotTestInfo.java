package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.AbstractJsonTestInfo;

public class BuildBotTestInfo extends AbstractJsonTestInfo  {

  public BuildBotTestInfo(String testName) {
    super(testName);
  }

  @Override
  protected String getPath() {
    return null;
  }

  @Override
  protected void assignExpectedValues(String[] expected) {

  }
}
