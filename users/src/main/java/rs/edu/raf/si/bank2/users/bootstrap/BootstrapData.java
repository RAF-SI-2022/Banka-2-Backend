package rs.edu.raf.si.bank2.users.bootstrap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextListener;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.users.bootstrap.readers.CurrencyReader;
import rs.edu.raf.si.bank2.users.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.users.models.mariadb.*;
import rs.edu.raf.si.bank2.users.models.mariadb.Currency;
import rs.edu.raf.si.bank2.users.repositories.mariadb.*;

@Component
public class BootstrapData implements CommandLineRunner {

    public static final String forexApiKey = "OF6BVKZOCXWHD9NS";
    /**
     * TODO promeniti ovo pre produkcije. Promenjen admin mejl da bismo mu
     * zapravo imali pristup. Mogu
     * da podesim forwardovanje ako je potrebno nekom drugom jos pristup.
     */
    private static final String ADMIN_EMAIL = "anesic3119rn+banka2backend" + "+admin@raf.rs";
    /**
     * TODO promeniti password ovde da bude jaci! Eventualno TODO napraviti
     * da se auto-generise novi
     * password pri TODO svakoj migraciji.
     */
    private static final String ADMIN_PASS = "admin";

    private static final String ADMIN_FNAME = "Admin";
    private static final String ADMIN_LNAME = "Adminic";
    private static final String ADMIN_JMBG = "2902968000000";
    private static final String ADMIN_PHONE = "0657817522";
    private static final String ADMIN_JOB = "ADMINISTRATOR";
    private static final boolean ADMIN_ACTIVE = true;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;


    private final EntityManagerFactory entityManagerFactory;

    private final RedisConnectionFactory redisConnectionFactory;

    @Autowired
    public BootstrapData(
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder,
            EntityManagerFactory entityManagerFactory,
            RedisConnectionFactory redisConnectionFactory) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManagerFactory = entityManagerFactory;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void run(String... args) {
        System.out.println("Started user service");
    }

}
