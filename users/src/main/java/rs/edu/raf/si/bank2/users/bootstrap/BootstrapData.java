package rs.edu.raf.si.bank2.users.bootstrap;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.users.models.mariadb.Permission;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;
import rs.edu.raf.si.bank2.users.services.MailingService;

@Component
public class BootstrapData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapData.class);
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
    private final MailingService mailingService;


    @Autowired
    public BootstrapData(
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder, MailingService mailingService) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailingService = mailingService;
    }

    @Override
    public void run(String... args) {

        // Set up all permissions
        Set<PermissionName> allPermissions = EnumSet.allOf(PermissionName.class);
        System.out.println(allPermissions);

        for (PermissionName pn : allPermissions) {
            List<Permission> findPerm = permissionRepository.findByPermissionNames(Collections.singletonList(pn));

            if (!findPerm.isEmpty()) {
                logger.info("Permission " + pn + " already found");
                continue;
            }

            Permission addPerm = new Permission(pn);
            this.permissionRepository.save(addPerm);
            logger.info("Permission " + pn + "added");
        }

        mailingService.getRegistrationCodes().add("1934");
        mailingService.getRegistrationCodes().add("1358");
        mailingService.getRegistrationCodes().add("5743");
        mailingService.getRegistrationCodes().add("4325");
        mailingService.getRegistrationCodes().add("2368");
        mailingService.getRegistrationCodes().add("2342");
        mailingService.getRegistrationCodes().add("2357");
        mailingService.getRegistrationCodes().add("3341");
        mailingService.getRegistrationCodes().add("1231");

        // Set up admin user
        Optional<User> adminUser = userRepository.findUserByEmail(ADMIN_EMAIL);
        if (adminUser.isPresent()) {
            logger.info("Root admin already added");
            return;
        }

        // Build root user object
        User admin = User.builder()
                .email(ADMIN_EMAIL)
                .firstName(ADMIN_FNAME)
                .lastName(ADMIN_LNAME)
                .password(this.passwordEncoder.encode(ADMIN_PASS))
                .jmbg(ADMIN_JMBG)
                .phone(ADMIN_PHONE)
                .jobPosition(ADMIN_JOB)
                .active(ADMIN_ACTIVE)
                .dailyLimit(1000000d) // usd
                // .defaultDailyLimit(10000D) // usd
                .defaultDailyLimit(1000000d) // usd
                .build();

        // Set admin's perms
        List<Permission> permissions = new ArrayList<>();
        permissions.add(permissionRepository
                .findByPermissionNames(Collections.singletonList(PermissionName.ADMIN_USER))
                .get(0));
        admin.setPermissions(permissions);

        // Save admin
        this.userRepository.save(admin);
        logger.info("Root admin added");


    }
}
