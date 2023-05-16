package rs.edu.raf.si.bank2.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories("rs.edu.raf.si.bank2.main.repositories.mariadb")
@EnableMongoRepositories("rs.edu.raf.si.bank2.main.repositories.mongodb")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
