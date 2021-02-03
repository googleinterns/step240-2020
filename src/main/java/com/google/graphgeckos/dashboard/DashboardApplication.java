package com.google.graphgeckos.dashboard;

import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.fetchers.github.GitHubClient;
import com.google.graphgeckos.dashboard.reader.BuildBotSetUpData;
import com.google.graphgeckos.dashboard.reader.BuildBotSetUpReader;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


@SpringBootApplication
@Configuration
@EnableScheduling
@EnableDatastoreRepositories
public class DashboardApplication implements SchedulingConfigurer {

  private static final String BASE_URL = "http://lab.llvm.org:8011/api/v2";

  private static final String GITHUB_URL = "https://api.github.com/repos/llvm/llvm-project/commits";

  private static final Logger logger = Logger.getLogger(DashboardApplication.class.getName());

  @Autowired private DatastoreRepository datastoreRepository;

  public static void main(String[] args) {
    SpringApplication.run(DashboardApplication.class, args);
  }

  /** Periodically fetches build bot status every 2 minutes. */
  @Scheduled(fixedDelay = 2 * 60 * 1000)
  void runBuildBotsFetchers() {
    logger.info("Fetching latest build bots status...");
    List<BuildBotSetUpData> buildBots = new BuildBotSetUpReader().read();
    if (buildBots == null) {
      throw new IllegalArgumentException(
          "List of build bots to run can't be null. Check the config file.");
    }
    for (BuildBotSetUpData buildBot : buildBots) {
      logger.info("Fetching builds from build bot: " + buildBot.name);
      new BuildBotClient(BASE_URL, datastoreRepository)
          .run(buildBot.name, buildBot.initialId, buildBot.delay);
    }
  }

  /** Periodically fetches GitHub commits every minute. */
  @Scheduled(fixedDelay = 60 * 1000)
  void runGitHubFetcher() {
    logger.info("Fetching latest GitHub commits...");
    new GitHubClient(GITHUB_URL, datastoreRepository).run(30);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
  }

  @Bean(destroyMethod = "shutdown")
  public Executor taskExecutor() {
    // Set up a pool of 2 threads, one for the build bot fetchers and one for the GitHub fetcher.
    return Executors.newScheduledThreadPool(2);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {};
  }
}
