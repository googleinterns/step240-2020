package com.google.graphgeckos.dashboard.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
public class DataRepositoryConfig {

  @Bean("dataRepository")
  @ApplicationScope
  public DataRepository getDataRepository() {
    // TODO: return new DataRepositoryImplementation();
    return null;
  }
}
