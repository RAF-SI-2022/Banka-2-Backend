package com.raf.si.Banka2Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.raf.si.Banka2Backend.repositories.users")
@EnableMongoRepositories("com.raf.si.Banka2Backend.repositories.exchange")
public class Banka2BackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(Banka2BackendApplication.class, args);
  }
}
