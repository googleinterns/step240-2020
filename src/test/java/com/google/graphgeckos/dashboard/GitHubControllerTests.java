package com.google.graphgeckos.dashboard;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.google.graphgeckos.dashboard.fetchers.github.GitHubController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GitHubController.class)
@AutoConfigureMockMvc
public class GitHubControllerTests {
  @Autowired
  private MockMvc mvc;

  private final GitHubJsonInfo JSON_WITHOUT_UNKNOWN_FIELDS = new GitHubJsonInfo("explicit_json");

  @Test
  public void json_without_unknown_fields_is_parsed_correctly() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/github-info")
      .contentType("application/json")
      .content(JSON_WITHOUT_UNKNOWN_FIELDS.getContent())
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("branch").value(JSON_WITHOUT_UNKNOWN_FIELDS.getBranch()))
      .andExpect(jsonPath("commitHash").value(JSON_WITHOUT_UNKNOWN_FIELDS.getCommitHash()))
      .andExpect(jsonPath("timestamp").value(JSON_WITHOUT_UNKNOWN_FIELDS.getTimestamp()))
      .andExpect(jsonPath("repositoryLink").value(JSON_WITHOUT_UNKNOWN_FIELDS.getRepositoryLink()));
  }

  private final GitHubJsonInfo REAL_JSON = new GitHubJsonInfo("real_json");

  @Test
  public void real_json_is_parsed_correctly() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/github-info")
      .contentType("application/json")
      .content(REAL_JSON.getContent())
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("branch").value(REAL_JSON.getBranch()))
      .andExpect(jsonPath("commitHash").value(REAL_JSON.getCommitHash()))
      .andExpect(jsonPath("timestamp").value(REAL_JSON.getTimestamp()))
      .andExpect(jsonPath("repositoryLink").value(REAL_JSON.getRepositoryLink()));
  }
}
