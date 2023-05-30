package rs.edu.raf.si.bank2.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Users microservice.
 * <p>
 * This application currently only supports authentication (see
 * AuthenticationController and ServiceAuthController), but should be
 * extended with full User manipulation CRUD.
 * <p>
 * The idea is that all services should communicate with this service for
 * authenticating user requests. Also, any user requests related to updating
 * core user data (e.g. email, password, username, name, citizen ID) should
 * also land here.
 */
@SpringBootApplication
@EnableJpaRepositories("rs.edu.raf.si.bank2.users.repositories.mariadb")
@EnableMongoRepositories("rs.edu.raf.si.bank2.users.repositories.mongodb")
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }
}
