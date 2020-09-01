package com.google.graphgeckos.dashboard.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataRepositoryConfig {

  @Bean("dataRepo")
  public DataRepository getRepository() {
    // TODO: return new DataRepositoryImplementation();
    return null;
  }
}
