package com.google.graphgeckos.dashboard.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuildBotSetUpReader {
  private static String JSON_PATH = "src/main/resources/static/buildbots.json";

  private BuildBotSetUpReader() {}

  private static List<BuildBotSetUpData> deserialize(String json) {
    try {
      List<?> rawBotsData = new ObjectMapper().readValue(json, List.class);
      List<BuildBotSetUpData> botSetUpDataList = new ArrayList<>();
      for (Object bot : rawBotsData) {
        botSetUpDataList.add(new ObjectMapper().readValue((String) bot, BuildBotSetUpData.class));
      }
      return botSetUpDataList;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static List<BuildBotSetUpData> read() throws IOException {
    return deserialize(Files.lines(Paths.get(JSON_PATH)).collect(Collectors.joining("\n")));
  }

}
