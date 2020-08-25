package com.google.graphgeckos.dashboard;

import com.google.graphgeckos.dashboard.storage.BuildInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuilderController {

  // TODO: mashasamsikova implement it
  @GetMapping("/builders")
  public BuildInfo getBuildInfo() {
    return BuildInfo();
  }

}
