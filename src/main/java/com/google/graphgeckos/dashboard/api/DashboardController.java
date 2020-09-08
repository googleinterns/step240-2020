package com.google.graphgeckos.dashboard.api;

import com.google.graphgeckos.dashboard.datatypes.BuildInfo;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

  /**
   * Provides access to information in the storage.
   */
  @Autowired
  private DatastoreRepository DATA_REPOSITORY;

  /**
   * Handles GET requests from the frontend part of the application.
   * Gets information from the database via {@code DATA_REPOSITORY}.
   * @param number the number of database entries to retrieve.
   * @param offset the offset from the latest database entry, for which to consider
   *               the requested number of entries.
   * @return list of a list containing at most {@code number} entries starting from the latest
   * entry - {@code offset}. If the database has not enough entries for the requested {@code offset}
   * and {@code number}, returns all available entries from that range.
   */
  @RequestMapping(value = "/builders/number={number}/offset={offset}",
    method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<BuildInfo> getBuildInfo(@PathVariable int number, @PathVariable int offset) {
    return DATA_REPOSITORY.getLastRevisionEntries(number, offset);
  }

}