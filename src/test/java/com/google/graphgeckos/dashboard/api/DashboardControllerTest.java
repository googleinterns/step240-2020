package com.google.graphgeckos.dashboard.api;

import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.*;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc
public class DashboardControllerTest {

  @MockBean
  private DatastoreRepository datastoreRepository;

  @Autowired
  private MockMvc mvc;

  private final String COMMIT_HASH_A = "1234";
  private final String COMMIT_HASH_B = "abcd";

  private final Timestamp TIMESTAMP = Timestamp.now();

  private final String BRANCH_A = "branch a";
  private final String BRANCH_B = "branch b";

  private final List<Log> LOGS_A = Arrays.asList(new Log("type 1", "link1"),
                                               new Log("type 2", "link2"));
  private final List<Log> LOGS_B = Collections.singletonList(new Log("type 3", "link3"));

  private final GitHubData INITIAL_GITHUB_DATA_A = new GitHubData(COMMIT_HASH_A, TIMESTAMP, BRANCH_A);
  private final GitHubData INITIAL_GITHUB_DATA_B = new GitHubData(COMMIT_HASH_B, TIMESTAMP, BRANCH_B);

  private final BuildInfo BUILD_INFO_A = new BuildInfo(INITIAL_GITHUB_DATA_A);
  private final BuildInfo BUILD_INFO_B = new BuildInfo(INITIAL_GITHUB_DATA_B);

  private final BuildBotData BUILDER_A = new BuildBotData(
                                        COMMIT_HASH_A, "Builder A", LOGS_A, BuilderStatus.PASSED);
  private final BuildBotData BUILDER_B = new BuildBotData(
                                         COMMIT_HASH_B, "Builder B", LOGS_B, BuilderStatus.FAILED);

  private final int ZERO_REVISIONS = 0;
  private final int ONE_REVISION = 1;
  private final int TWO_REVISIONS = 2;
  private final int OFFSET_ZERO = 0;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setUpRepository() {
    BUILD_INFO_A.addBuilder(BUILDER_A);
    BUILD_INFO_B.addBuilder(BUILDER_B);
  }

  @Test
  public void whenGivenZeroRevisionsOffsetZeroReturnsEmptyJson() throws Exception {

    given(datastoreRepository.getLastRevisionEntries(ZERO_REVISIONS, OFFSET_ZERO))
      .willReturn(Collections.emptyList());

    ResultActions result = mvc.perform(
      MockMvcRequestBuilders.get("/builders/number={number}/offset={offset}",
        ZERO_REVISIONS, OFFSET_ZERO)
        .accept(MediaType.APPLICATION_JSON));

    String emptyJsonList = "[]";
    result.andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string(emptyJsonList));
  }

  @Test
  public void whenGivenOneRevisionOffsetZeroReturnsOneBuildInfoJson() throws Exception {

    given(datastoreRepository.getLastRevisionEntries(ONE_REVISION, OFFSET_ZERO))
      .willReturn(Collections.singletonList(BUILD_INFO_A));

    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/builders/number={number}/offset={offset}",
          ONE_REVISION, OFFSET_ZERO)
       .accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$[0].commitHash").value(COMMIT_HASH_A))
          .andExpect(jsonPath("$[0].branch").value(BRANCH_A));

  }
}
