// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.graphgeckos.dashboard.fetchers.buildbot;

import org.springframework.lang.NonNull;
import java.util.Arrays;
import java.util.Objects;

/** BuildBot fetchers creator. Create a population of different fetchers with common baseUrl. */
public class BuildBotClientPopulation {

  /** Base url of the BuildBot API. */
  private final String baseUrl;

  public BuildBotClientPopulation(@NonNull String baseUrl) {
    Objects.requireNonNull(baseUrl);

    this.baseUrl = baseUrl;
  }

  public BuildBotClientPopulation() {
    this("http://lab.llvm.org:8011/json/builders/");
  }

  /**
   * Creates and runs fetchers to fetch data from the BuildBots with the given name and initial build id,
   * provided in the form of {@link BuildBotInitializer}.
   *
   * @param buildBots information about the BuildBots to fetch data from.
   */
  public void populate(@NonNull BuildBotInitializer... buildBots) {
    Objects.requireNonNull(buildBots);

    if (buildBots.length == 0) {
      throw new IllegalArgumentException("Expected one or more BuildBots, found zero");
    }
    Arrays.stream(buildBots)
          .forEach(x -> new BuildBotClient(baseUrl)
          .run(x.name, x.initialBuildId, x.delay));
  }

}
