package com.google.graphgeckos.dashboard;

import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.fetchers.github.GitHubClient;
import com.google.graphgeckos.dashboard.reader.BuildBotSetUpData;
import com.google.graphgeckos.dashboard.reader.BuildBotSetUpReader;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableDatastoreRepositories
public class DashboardApplication {

  private static final String BASE_URL = "http://lab.llvm.org:8011/api/v2";

  private static final String GITHUB_URL = "https://api.github.com/repos/llvm/llvm-project/commits";

  @Autowired private DatastoreRepository datastoreRepository;

  public static void main(String[] args) {
    SpringApplication.run(DashboardApplication.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      new Thread(
              () -> {
                new GitHubClient(GITHUB_URL, datastoreRepository).run(30);
              })
          .start();
      new Thread(
              () -> {
                List<BuildBotSetUpData> buildBotToRun = new BuildBotSetUpReader().read();
                if (buildBotToRun == null) {
                  throw new IllegalArgumentException(
                      "List of BuildBots to run can't be null. Check the config file.");
                }
                buildBotToRun.forEach(
                    x ->
                        new BuildBotClient(BASE_URL, datastoreRepository)
                            .run(x.name, x.initialId, x.delay));
              })
          .start();
    };
  }
}
