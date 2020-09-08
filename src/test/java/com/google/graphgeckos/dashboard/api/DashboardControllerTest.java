package com.google.graphgeckos.dashboard.api;

import com.google.cloud.Timestamp;
import com.google.graphgeckos.dashboard.datatypes.*;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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

  private final String COMMIT_HASH = "1234";

  private final Timestamp TIMESTAMP = Timestamp.now();

  private final String BRANCH = "branch a";

  private final String NAME_A = "Builder A";
  private final String NAME_B = "Builder B";

  private final String STATUS_A = "PASSED";
  private final String STATUS_B = "FAILED";

  private final String LOG_TYPE_1 = "type 1";
  private final String LOG_TYPE_2 = "type 2";
  private final String LOG_TYPE_3 = "type 3";
  private final String LOG_LINK_1 = "link 1";
  private final String LOG_LINK_2 = "link 2";
  private final String LOG_LINK_3 = "link 3";

  private final List<Log> LOGS_A = Arrays.asList(new Log(LOG_TYPE_1, LOG_LINK_1),
                                               new Log(LOG_TYPE_2, LOG_LINK_2));
  private final List<Log> LOGS_B = Collections.singletonList(new Log(LOG_TYPE_3, LOG_LINK_3));

  private final GitHubData INITIAL_GITHUB_DATA = new GitHubData(COMMIT_HASH, TIMESTAMP, BRANCH);

  private final BuildInfo BUILD_INFO = new BuildInfo(INITIAL_GITHUB_DATA);

  private final BuildBotData BUILDER_A = new BuildBotData(
                                        COMMIT_HASH, NAME_A, LOGS_A, BuilderStatus.valueOf(STATUS_A));
  private final BuildBotData BUILDER_B = new BuildBotData(
                                         COMMIT_HASH, NAME_B, LOGS_B, BuilderStatus.valueOf(STATUS_B));

  private final int MINUS_ONE_REVISION = -1;
  private final int ZERO_REVISIONS = 0;
  private final int ONE_REVISION = 1;
  private final int OFFSET_ZERO = 0;

  /** Enables Mockito.*/
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setUpBuildInfo() {
    BUILD_INFO.addBuilder(BUILDER_A);
    BUILD_INFO.addBuilder(BUILDER_B);
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
      .willReturn(Collections.singletonList(BUILD_INFO));

    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/builders/number={number}/offset={offset}",
          ONE_REVISION, OFFSET_ZERO)
       .accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$[0].commitHash").value(COMMIT_HASH))
          .andExpect(jsonPath("$[0].branch").value(BRANCH))
          .andExpect(jsonPath("$[0].builders[0].name").value(NAME_A))
          .andExpect(jsonPath("$[0].builders[1].status").value(STATUS_B))
          .andExpect(jsonPath("$[0].builders[0].logs[1].type").value(LOG_TYPE_2))
          .andExpect(jsonPath("$[0].builders[0].logs[0].link").value(LOG_LINK_1));
  }

  @Test
  public void setsRequestTypeToBadRequestWhenIllegalArgumentExceptionIsThrownByDatastoreRepository() throws Exception {
    given(datastoreRepository.getLastRevisionEntries(MINUS_ONE_REVISION, OFFSET_ZERO))
      .willThrow(new IllegalArgumentException());

    mvc.perform(
      MockMvcRequestBuilders.get("/builders/number={number}/offset={offset}",
        MINUS_ONE_REVISION, OFFSET_ZERO)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

}
