package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class BuildBotClientTest {

  public static MockWebServer webServer;

  @BeforeAll
  static void setUp() {
    webServer = new MockWebServer();
  }

  @AfterAll
  public static void tearDown() throws IOException {
    webServer.shutdown();
  }

  private final BuildBotTestInfo JSON_WITHOUT_UNKNOWN_FIELDS =
    new BuildBotTestInfo("no_unknown_fields_json");

  @Test
  public void jsonWithoutUnknownFieldsIsParsedCorrectly() {
    String baseUrl = String.format("http://localhost:%s", webServer.getPort());
    BuildBotClient.setBaseUrl(baseUrl);

    webServer.enqueue(new MockResponse()
      .setBody(JSON_WITHOUT_UNKNOWN_FIELDS.getContent())
      .setHeader("Content-Type", "application/json"));
  }


}
