package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class BuildBotClientTest {

  DatastoreRepository datastoreRepository = Mockito.mock(DatastoreRepository.class);

  @InjectMocks
  BuildBotClient client = new BuildBotClient();

  @Captor
  ArgumentCaptor<BuildBotData> buildBotDataCaptor;

  @Test
  public void allFieldsAreOfExpectedFormat() {
    client.run("clang-x86_64-debian-fast", 36624);
//    Mockito.verify(datastoreRepository).updateRevisionEntry(buildBotDataCaptor.capture());
//    BuildBotData capturedData = buildBotDataCaptor.getValue();

//    System.out.println(capturedData);
  }

}
