package rs.edu.raf.si.bank2.Bank2Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories("rs.edu.raf.si.bank2.Bank2Backend.repositories.mariadb")
@EnableMongoRepositories("rs.edu.raf.si.bank2.Bank2Backend.repositories.mongodb")
public class Bank2BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Bank2BackendApplication.class, args);
    }
}
