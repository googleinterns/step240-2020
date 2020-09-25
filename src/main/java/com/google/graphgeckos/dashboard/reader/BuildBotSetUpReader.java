package com.google.graphgeckos.dashboard.reader;

import com.google.api.client.util.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;

public class BuildBotSetUpReader {
  /** Path to the config file with the initial BuildBot data. */
  private static String filePath = "src/main/resources/static/buildbots.txt";

  private BuildBotSetUpReader() {}


  /**
   * Deserializes config file.
   *
   * @param info is a config file. Values in each BuildBot info block come in the following order:
   *             name of the BuildBot, initial id, delay in seconds
   * @return list of data that is needed for initializing and running instances of
   *          {@link com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient}.
   */
  private static List<BuildBotSetUpData> deserialize(String info) {
    List<BuildBotSetUpData> bots = new ArrayList<>();
    String[] splited = info.split(System.lineSeparator());
    if (splited.length < 3 || splited.length % 3 != 0) {
      throw new IllegalArgumentException("Every buildbot has a name, an initial id and a delay");
    }

    for (int i = 0; i < splited.length - 2; i += 3) {
      bots.add(new BuildBotSetUpData(splited[i], Long.parseLong(splited[i + 1]), Long.parseLong(splited[i + 2])));
    }
    return bots;
  }

  /**
   * Creates list of data that is needed for initializing and running instances of
   * {@link com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient}.
   *
   * @return a list of data for initializing BuildBot fetchers.
   */
  public static List<BuildBotSetUpData> read() {
    try {
      return deserialize(Files.lines(Paths.get(filePath)).collect(Collectors.joining(System.lineSeparator())));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void setJsonPath(@NonNull String filePath) {
    BuildBotSetUpReader.filePath = Preconditions.checkNotNull(filePath);
  }

}
