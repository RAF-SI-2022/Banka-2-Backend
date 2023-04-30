package rs.edu.raf.si.bank2.securities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories("rs.edu.raf.si.bank2.securities.repositories.mariadb")
@EnableMongoRepositories("rs.edu.raf.si.bank2.securities.repositories.mongodb")
public class SecuritiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(rs.edu.raf.si.bank2.securities.SecuritiesApplication.class, args);
    }
}
