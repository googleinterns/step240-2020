package com.google.graphgeckos.dashboard.fetchers.github;

import com.google.graphgeckos.dashboard.fetchers.github.CommitData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubController {

  /**
   * Handles POST requests from the GitHub API. Setting GitHub Webhooks is required.
   * Extracts required data from the json {@see CommitData} and adds it to the Storage.
   * @param commitData json from the GitHub API.
   */
  @PostMapping("/github-info")
  public CommitData postGitHubInfo(@RequestBody CommitData commitData) {
    System.out.println(commitData);
    // TODO(issue #64): implement described functionality.
    assert false;
    return commitData;
  }

}
