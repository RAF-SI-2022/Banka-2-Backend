package com.raf.si.Banka2Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.raf.si.Banka2Backend.repositories.mariadb")
@EnableMongoRepositories("com.raf.si.Banka2Backend.repositories.mongodb")
public class Banka2BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Banka2BackendApplication.class, args);
        String t = "" + "xxasas";

        {
            String x = new String("yyyy") + "yooo";
        }

        ;
    }
}
