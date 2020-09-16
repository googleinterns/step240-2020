package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class BuildBotClientTest {

  @MockBean
  DatastoreRepository datastoreRepository;

  @InjectMocks
  BuildBotClient client;

  @Test
  public void allFieldAreOfExpectedFormat() {
    BuildBotClient.run("llvm-clang-x86_64-win-fast", 23346);

  }

}
