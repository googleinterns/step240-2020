package com.google.graphgeckos.dashboard.api;

import com.google.graphgeckos.dashboard.storage.BuildInfo;
import com.google.graphgeckos.dashboard.storage.DataRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuilderController {

  private static DataRepository dataRepository;

  // TODO: mashasamsikova implement it
  @GetMapping("/builders/number={number}/offset={offset}")
  public Iterable<BuildInfo> getBuildInfo(@PathVariable int number, @PathVariable int offset) {
    return dataRepository.getLastRevisionEntries(number, offset);
  }

}
