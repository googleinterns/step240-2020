package com.google.graphgeckos.dashboard.fetchers.github;

import com.google.graphgeckos.dashboard.datatypes.GitHubData;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubController {

  /**
   * Handles POST requests from the GitHub API. Setting GitHub Webhooks is required.
   * Extracts required data from the json {@see CommitData} and adds it to the Storage.
   * @param gitHubData json from the GitHub API.
   */
  @RequestMapping(value = "/github-info", method = RequestMethod.POST, headers = {"content-type=application/json"})
  public GitHubData postGitHubInfo(@RequestBody GitHubData gitHubData) {
    // TODO(issue #64): Implement described functionality.
    return gitHubData;
  }

}
