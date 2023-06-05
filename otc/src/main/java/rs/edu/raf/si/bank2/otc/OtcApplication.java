package rs.edu.raf.si.bank2.otc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("rs.edu.raf.si.bank2.otc.repositories.mariadb")
@EnableMongoRepositories("rs.edu.raf.si.bank2.otc.repositories.mongodb")
public class OtcApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtcApplication.class, args);
    }
}
