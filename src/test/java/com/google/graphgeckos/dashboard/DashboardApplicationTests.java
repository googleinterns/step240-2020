package com.google.graphgeckos.dashboard;

import com.google.graphgeckos.dashboard.api.DashboardControllerTest;
import com.google.graphgeckos.dashboard.github.GitHubControllerTests;
import com.google.graphgeckos.dashboard.storage.DatastoreRepositoryTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DatastoreRepositoryTests.class,
    GitHubControllerTests.class,
    DashboardControllerTest.class,
  })
class DashboardApplicationTests {

  /* Runs all tests from the classes from the @Site.SuiteClasses list. */
  @Test
  public void contextLoads() {}
}
