package rs.edu.raf.si.bank2.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories("rs.edu.raf.si.bank2.users.repositories.mariadb")
@EnableMongoRepositories("rs.edu.raf.si.bank2.users.repositories.mongodb")
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }
}
