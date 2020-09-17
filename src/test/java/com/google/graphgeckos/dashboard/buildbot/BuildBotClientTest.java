package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.argThat;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class BuildBotClientTest {

  class BuildBotDataMatcher implements ArgumentMatcher<BuildBotData> {

    @Override
    public boolean matches(BuildBotData data) {
      return data.isValid();
    }
  }

  @InjectMocks
  BuildBotClient client = new BuildBotClient();

  @Mock
  DatastoreRepository datastoreRepository;



//  /** Enables Mockito. */
//  @Before
//  public void init() {
//    MockitoAnnotations.initMocks(this);
//  }

  @Test
  public void allFieldsAreOfExpectedFormat() {
    client.run("clang-x86_64-debian-fast", 36624);
    Mockito.verify(datastoreRepository, Mockito.after(BuildBotClient.getRequestFrequency() * 2000 - 10))
      .updateRevisionEntry(argThat(new BuildBotDataMatcher()));
  }

}
