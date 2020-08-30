package com.google.graphgeckos.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class GitHubFetcherTests {
  @Autowired
  private MockMvc mvc;

  private static class JsonFactory {
    private JsonFactory() {}

    static String create(String branch, String commitHash, String timestamp, String repositoryLink) {
      return String.format("{\"ref\": \"%s\", \"head_commit\": " +
        "{\"id\": \"%s\", \"timestamp\": \"%s\"}, " +
        "\"repository\": {\"html_url\": \"%s\"}}", branch, commitHash, timestamp, repositoryLink);
    }
  }

  private final String BRANCH = "ref";
  private final String COMMIT_HASH = "123h3";
  private final String TIMESTAMP = "00.00";
  private final String REPOSITORY_LINK = "github/repo";
  private final String JSON_WITHOUT_UNKNOWN_FIELDS =
    JsonFactory.create(BRANCH, COMMIT_HASH, TIMESTAMP, REPOSITORY_LINK);

  @Test
  public void json_without_unknown_fields_is_parsed_correctly() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/github-info")
      .contentType("application/json")
      .content(JSON_WITHOUT_UNKNOWN_FIELDS)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("branch").value(BRANCH))
      .andExpect(jsonPath("commitHash").value(COMMIT_HASH))
      .andExpect(jsonPath("timestamp").value(TIMESTAMP))
      .andExpect(jsonPath("repositoryLink").value(REPOSITORY_LINK));
  }
}
