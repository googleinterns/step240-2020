package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BuildBotClientTest {

  static class BuildBotDataMatcher implements ArgumentMatcher<BuildBotData> {

    @Override
    public boolean matches(BuildBotData data) {
      return data.isValid();
    }
  }

  @InjectMocks
  BuildBotClient client;

  @Mock
  DatastoreRepository datastoreRepository;

  /** Enables Mockito. */
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  public final String VALID_BUILD_BOT = "clang-x86_64-debian-fast";
  public final int VALID_BUILD_ID = 36624;

  @Test
  public void verifyValidUpdateCallToRepository() {
    client.run(VALID_BUILD_BOT, VALID_BUILD_ID);
    Mockito.verify(datastoreRepository, Mockito.after(client.getRequestFrequency() * 2000 - 10))
      .updateRevisionEntry(Mockito.argThat(new BuildBotDataMatcher()));
  }

}
