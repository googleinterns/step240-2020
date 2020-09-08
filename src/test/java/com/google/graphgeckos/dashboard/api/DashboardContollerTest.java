package com.google.graphgeckos.dashboard.api;

import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.*;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc
public class DashboardContollerTest {

  @Autowired
  private MockMvc mvc;

  private static final String COMMIT_HASH_A = "1234";
  private static final String COMMIT_HASH_B = "abcd";

  private static final Timestamp TIMESTAMP = Timestamp.now();

  private static final String BRANCH_A = "branch a";
  private static final String BRANCH_B = "branch b";

  private static final List<Log> LOGS_A = Arrays.asList(new Log("type 1", "link1"),
                                               new Log("type 2", "link2"));
  private static final List<Log> LOGS_B = Collections.singletonList(new Log("type 3", "link3"));

  private static final GitHubData INITIAL_GITHUB_DATA_A = new GitHubData(COMMIT_HASH_A, TIMESTAMP, BRANCH_A);
  private static final GitHubData INITIAL_GITHUB_DATA_B = new GitHubData(COMMIT_HASH_B, TIMESTAMP, BRANCH_B);

  private static final BuildInfo BUILD_INFO_A = new BuildInfo(INITIAL_GITHUB_DATA_A);
  private static final BuildInfo BUILD_INFO_B = new BuildInfo(INITIAL_GITHUB_DATA_B);

  private static final BuildBotData BUILDER_A = new BuildBotData(
                                        COMMIT_HASH_A, "Builder A", LOGS_A, BuilderStatus.PASSED);
  private static final BuildBotData BUILDER_B = new BuildBotData(
                                         COMMIT_HASH_B, "Builder B", LOGS_B, BuilderStatus.FAILED);

  private final int ONE_REVISION = 1;
  private final int TWO_REVISIONS = 2;
  private final int OFFSET_ZERO = 0;

  @BeforeClass
  public static void createBuildInfos() {
    BUILD_INFO_A.addBuilder(BUILDER_A);
    BUILD_INFO_B.addBuilder(BUILDER_B);
  }

  @MockBean
  private DatastoreRepository datastoreRepository;


  @Before
  public void setUpRepository() {
    Mockito.when(
      datastoreRepository.getLastRevisionEntries(ONE_REVISION, OFFSET_ZERO))
        .thenReturn(Collections.singletonList(BUILD_INFO_A));

    Mockito.when(
      datastoreRepository.getLastRevisionEntries(TWO_REVISIONS, OFFSET_ZERO))
      .thenReturn(Arrays.asList(BUILD_INFO_A, BUILD_INFO_B));
  }

  @Test
  public void whenGivenOneRevisionOffsetZeroReturnsOneBuildInfo() throws Exception {
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/builders/number={number}/offset={offset}",
          ONE_REVISION, OFFSET_ZERO)
       .contentType("application/json")
       .accept(MediaType.APPLICATION_JSON));
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("[0].commitHash").value(COMMIT_HASH_A));
  }
}
