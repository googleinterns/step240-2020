package com.google.graphgeckos.dashboard.fetchers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubFetcher {

  /**
   * Handles POST requests from the GitHub API. Setting GitHub Webhooks is required.
   * Extracts required data from the json {@see CommitData} and adds it to the Storage.
   *
   * Class that provide access to the Storage is required to implement the functionality.
   * @param commitData json from the GitHub API
   */
  @PostMapping("/github-info")
  public void postGithubInfo(@RequestBody CommitData commitData) {
    System.out.println(commitData);
    // TODO: implement described functionality. (Couldn't be implemented now since there is no Class to access storage).
    assert false;
  }
}
