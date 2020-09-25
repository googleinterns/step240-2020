package com.google.graphgeckos.dashboard;

import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.reader.BuildBotSetUpData;
import com.google.graphgeckos.dashboard.reader.BuildBotSetUpReader;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@SpringBootApplication
@Configuration
@EnableDatastoreRepositories
public class DashboardApplication {

  private static final String BASE_URL = "http://lab.llvm.org:8011/builders";

  public static void main(String[] args) {
    SpringApplication.run(DashboardApplication.class, args);

    List<BuildBotSetUpData> buildBotToRun = new BuildBotSetUpReader().read();
    if (buildBotToRun == null) {
      throw new IllegalArgumentException("List of BuildBots to run can't be null. Check the config file.");
    }
    buildBotToRun.forEach(x -> new BuildBotClient(BASE_URL).run(x.name, x.initialId, x.delay));
  }

}
