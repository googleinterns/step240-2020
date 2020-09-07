package com.google.graphgeckos.dashboard.api;

import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.storage.DataRepository;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

  private static DataRepository dataRepository = new DatastoreRepository();

  @RequestMapping(value = "/builders/number={number}/offset={offset}",
    method = RequestMethod.GET, headers = {"content-type=application/json"})
  public Iterable<BuildInfo> getBuildInfo(@PathVariable int number, @PathVariable int offset) {
    return dataRepository.getLastRevisionEntries(number, offset);
  }

}