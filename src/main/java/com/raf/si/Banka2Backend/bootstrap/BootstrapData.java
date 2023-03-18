package com.raf.si.Banka2Backend.bootstrap;

import com.raf.si.Banka2Backend.models.users.Permission;
import com.raf.si.Banka2Backend.models.users.PermissionName;
import com.raf.si.Banka2Backend.models.users.User;
import com.raf.si.Banka2Backend.repositories.users.PermissionRepository;
import com.raf.si.Banka2Backend.repositories.users.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

  /**
   * TODO promeniti ovo pre produkcije. Promenjen admin mejl da bismo mu zapravo imali pristup. Mogu
   * da podesim forwardovanje ako je potrebno nekom drugom jos pristup.
   */
  private static final String ADMIN_EMAIL = "anesic3119rn+banka2backend+admin@raf.rs";
  /**
   * TODO promeniti password ovde da bude jaci! Eventualno TODO napraviti da se auto-generise novi
   * password pri TODO svakoj migraciji.
   */
  private static final String ADMIN_PASS = "admin";

  private static final String ADMIN_FNAME = "Admin";
  private static final String ADMIN_LNAME = "Adminic";
  private static final String ADMIN_JMBG = "2902968000000";
  private static final String ADMIN_PHONE = "0657817522";
  private static final String ADMIN_JOB = "administrator";
  private static final boolean ADMIN_ACTIVE = true;

  private final UserRepository userRepository;
  private final PermissionRepository permissionRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public BootstrapData(
      UserRepository userRepository,
      PermissionRepository permissionRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.permissionRepository = permissionRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {

    // Do this only on the first ever run of the app.
    // Includes both initial admin run and permissions run.
    Optional<User> adminUser = userRepository.findUserByEmail(ADMIN_EMAIL);
    if (adminUser.isPresent()) {
      System.out.println("Started!");
      return;
    }

    // Add admin
    User admin =
        User.builder()
            .email(ADMIN_EMAIL)
            .firstName(ADMIN_FNAME)
            .lastName(ADMIN_LNAME)
            .password(this.passwordEncoder.encode(ADMIN_PASS))
            .jmbg(ADMIN_JMBG)
            .phone(ADMIN_PHONE)
            .jobPosition(ADMIN_JOB)
            .active(ADMIN_ACTIVE)
            .build();

    // Add initial perms
    List<Permission> permissions = new ArrayList<>();
    Permission adminPermission = new Permission(PermissionName.ADMIN_USER);
    Permission readPermission = new Permission(PermissionName.READ_USERS);
    Permission createPermission = new Permission(PermissionName.CREATE_USERS);
    Permission updatePermission = new Permission(PermissionName.UPDATE_USERS);
    Permission deletePermission = new Permission(PermissionName.DELETE_USERS);
    permissions.add(adminPermission);
    this.permissionRepository.save(adminPermission);
    this.permissionRepository.save(readPermission);
    this.permissionRepository.save(createPermission);
    this.permissionRepository.save(updatePermission);
    this.permissionRepository.save(deletePermission);

    // Add admin perms
    admin.setPermissions(permissions);
    this.userRepository.save(admin);

    System.out.println("Loaded!");
  }
}
