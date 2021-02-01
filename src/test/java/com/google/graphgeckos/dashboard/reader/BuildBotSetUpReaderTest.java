package com.google.graphgeckos.dashboard.reader;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BuildBotSetUpReaderTest {

  private final BuildBotSetUpData BOT_A = new BuildBotSetUpData("clang-x86_64-debian-fast", 600, 0);
  private final BuildBotSetUpData BOT_B = new BuildBotSetUpData("mlir-nvidia", 600, 0);

  // Given: json with info of two BuildBots.
  // Expected: a list of two properly parsed BuildBot.
  @Test
  public void parsesGivenConfigFileProperlyIntoBuildBotSetUpData() {
    String filePath = "src/test/resources/jsons/reader/input_just_json.txt";
    BuildBotSetUpReader reader = new BuildBotSetUpReader(filePath);
    List<BuildBotSetUpData> result = reader.read();
    BuildBotSetUpData botA = result.get(0);
    BuildBotSetUpData botB = result.get(1);

    Assert.assertEquals(BOT_A, botA);
    Assert.assertEquals(BOT_B, botB);
  }
}
