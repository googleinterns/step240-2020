package com.google.graphgeckos.dashboard;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.fetchers.github.GitHubClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableDatastoreRepositories
public class DashboardApplication implements SchedulingConfigurer {

  /**
   * Base LLVM Buildbot API URL.
   */
  private static final String BASE_URL = "https://lab.llvm.org/buildbot/api/v2";

  /**
   * LLVM Github commits URL.
   */
  private static final String GITHUB_URL = "https://api.github.com/repos/llvm/llvm-project/commits";

  /**
   * Path to the dashboard configuration file, relative to src/main/resources.
   *
   * <p>The configuration file is an UTF-8 encoded text file. It lists the names of the build bots
   * to load, one per line.
   */
  private static final String CONFIG_PATH = "static/buildbots.txt";

  private static final Logger logger = Logger.getLogger(DashboardApplication.class.getName());

  @Autowired private DatastoreRepository datastoreRepository;

  public static void main(String[] args) {
    SpringApplication.run(DashboardApplication.class, args);
  }

  /**
   * Reads the build bot names from the configuration.
   *
   * @param configuration dashboard configuration, listing the names of the build bots to load, one
   *     per line.
   */
  static String[] readBuildBotNames(String configuration) {
    return configuration.split("\\n");
  }

  /** 
   * Periodically fetches build bot status every 10 minutes.
   *
   *
   * @throws IllegalStateException if the configuration at CONFIG_PATH cannot be read or is empty.
   */
  @Scheduled(fixedDelay = 10 * 60 * 1000)
  void runBuildBotsFetchers() {
    logger.info("Fetching latest build bots status...");
    String configuration;
    try {
      configuration =
          CharStreams.toString(
              new InputStreamReader(
                  new ClassPathResource(CONFIG_PATH).getInputStream(), Charsets.UTF_8));
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read configuration from: " + CONFIG_PATH, e);
    }

    String[] buildBots = readBuildBotNames(configuration);
    if (buildBots.length == 0) {
      throw new IllegalStateException(
          "List of build bots to run can't be empty. Check the config file: " + CONFIG_PATH);
    }

    for (String buildBot : buildBots) {
      logger.info("Fetching builds from build bot: " + buildBot);
      new BuildBotClient(BASE_URL, datastoreRepository).run(buildBot);
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
