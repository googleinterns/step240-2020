package com.google.graphgeckos.dashboard.api;

import com.google.graphgeckos.dashboard.storage.BuildInfo;
import com.google.graphgeckos.dashboard.storage.DataRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class BuilderController {

  // Bean with @ApplicationScope?
  @Resource(name = "dataRepository")
  // TODO: change to private static DataRepository dataRepository = new DataRepositoryImplementation();
  private static DataRepository dataRepository;

  // TODO: [issue #69] implement interaction between REST API and Storage
  @RequestMapping(value = "/builders/number={number}/offset={offset}",
                  method = RequestMethod.GET, headers = {"content-type=application/json"})
  public Iterable<BuildInfo> getBuildInfo(@PathVariable int number, @PathVariable int offset) {
    return dataRepository.getLastRevisionEntries(number, offset);
  }

}
