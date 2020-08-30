package com.google.graphgeckos.dashboard.fetchers.github;

import org.springframework.web.bind.annotation.*;

@RestController
public class GitHubController {

  /**
   * Handles POST requests from the GitHub API. Setting GitHub Webhooks is required.
   * Extracts required data from the json {@see CommitData} and adds it to the Storage.
   * @param commitData json from the GitHub API.
   */
  @RequestMapping(value = "/github-info", method = RequestMethod.POST, headers = {"content-type=application/json"})
  public CommitData postGitHubInfo(@RequestBody CommitData commitData) {
    // TODO(issue #64): implement described functionality.
    return commitData;
  }

}
