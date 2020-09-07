package com.google.graphgeckos.dashboard;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableDatastoreRepositories
public class DashboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(DashboardApplication.class, args);
  }

}
