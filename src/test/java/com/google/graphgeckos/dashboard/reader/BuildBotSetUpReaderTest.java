package com.google.graphgeckos.dashboard.reader;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BuildBotSetUpReaderTest {

  private final BuildBotSetUpData BOT_A = new BuildBotSetUpData("clang-x86_64-debian-fast", 600, 0);
  private final BuildBotSetUpData BOT_B = new BuildBotSetUpData("mlir-nvidia", 600, 0);

  @Test
  public void deserializeDataToExpectedListOfBuildBotSetUpData() {
    BuildBotSetUpReader.setJsonPath("src/test/resources/jsons/reader/input_just_json.txt");
    List<BuildBotSetUpData> result = BuildBotSetUpReader.read();
    BuildBotSetUpData botA = result.get(0);
    BuildBotSetUpData botB = result.get(1);

    Assert.assertEquals(BOT_A, botA);
    Assert.assertEquals(BOT_B, botB);
  }
}
