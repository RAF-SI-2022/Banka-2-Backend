package com.raf.si.Banka2Backend.bootstrap;

import com.raf.si.Banka2Backend.bootstrap.readers.CurrencyReader;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.models.mariadb.Exchange;
import com.raf.si.Banka2Backend.models.mariadb.Permission;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.repositories.mariadb.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.raf.si.Banka2Backend.services.ForexService;
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

  public static final String forexApiKey = "6DL0Q8YP76H9K9T6";

  private final UserRepository userRepository;
  private final PermissionRepository permissionRepository;
  private final CurrencyRepository currencyRepository;
  private final InflationRepository inflationRepository;
  private final PasswordEncoder passwordEncoder;
  private final ExchangeRepository exchangeRepository;
  private final FutureRepository futureRepository;

  private final ForexService forexService;

  @Autowired
  public BootstrapData(
          ForexService forexService,

      UserRepository userRepository,
      PermissionRepository permissionRepository,
      CurrencyRepository currencyRepository,
      InflationRepository inflationRepository,
      PasswordEncoder passwordEncoder,
      ExchangeRepository exchangeRepository,
      FutureRepository futureRepository) {
    this.forexService = forexService;
    this.userRepository = userRepository;
    this.permissionRepository = permissionRepository;
    this.currencyRepository = currencyRepository;
    this.inflationRepository = inflationRepository;
    this.passwordEncoder = passwordEncoder;
    this.exchangeRepository = exchangeRepository;
    this.futureRepository = futureRepository;
  }

  @Override
  public void run(String... args) throws Exception {


    // If empty, add futures in db from csv
    long numberOfRowsFutures = this.futureRepository.count();
    if (numberOfRowsFutures == 0) {
      System.out.println("Added futures");
      this.loadFutureTable();
    }

    // If empty, add currencies in db from csv
    long numberOfRowsCurrency = this.currencyRepository.count();
    if (numberOfRowsCurrency == 0) {
      System.out.println("Added currencies");
      this.loeadCurrenciesTable();
    }

    // If empty, add exchange markets in db from csv
    long numberOfExchanges = this.exchangeRepository.count();
    if (numberOfExchanges == 0) {
      System.out.println("Added exchange markets");
      this.loadExchangeMarkets();
    }

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

  private void loeadCurrenciesTable() throws IOException {
    CurrencyReader cs = new CurrencyReader();
    List<Currency> currencyList = cs.getCurrencies();
    this.currencyRepository.saveAll(currencyList);
    List<Inflation> inflationList = cs.getInflations();
    this.inflationRepository.saveAll(inflationList);
  }

  private void loadExchangeMarkets() throws IOException {
    // Do this only on the first ever run of the app.
    // read from file
    List<Exchange> exchanges =
        Files.lines(Paths.get("src/main/resources/csvs/exchange.csv"))
            .parallel()
            .skip(1)
            .map(line -> line.split(","))
            .filter(data -> exchangeRepository.findExchangeByMicCode(data[2]).isEmpty())
            .map(
                data ->
                    new Exchange(
                        data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]))
            .toList();

    // save into repository
    for (int i = 0; i < exchanges.size() - 1; i++) {
      for (int j = i + 1; j < exchanges.size(); j++) {
        Exchange e = exchanges.get(i);
        Exchange e1 = exchanges.get(j);
        if (e.getAcronym() == e1.getAcronym()) {
          System.out.println("id " + e.getId() + "id " + e1.getId());
        }
      }
    }
    exchangeRepository.saveAll(exchanges);
  }

  private void loadFutureTable() throws IOException {
    List<Future> futures =
        Files.lines(Paths.get("src/main/resources/csvs/future_data.csv"))
            .parallel()
            .skip(1)
            .map(line -> line.split(","))
            .filter(data -> futureRepository.findFutureByFutureName(data[0]).isEmpty())
            .map(
                data ->
                    new Future(
                        data[0],
                        Integer.parseInt(data[1]),
                        data[2],
                        Integer.parseInt(data[3]),
                        null,
                        true))
            .toList();

    futureRepository.saveAll(futures);

    randomiseFutureTableData();
  }

  private void randomiseFutureTableData(){
    List<Future> allFutures = new ArrayList<>();
    List<Future> newRandomisedFutures = new ArrayList<>();
    allFutures = futureRepository.findAll();
    Random randomGenerator = new Random();

    for (Future future : allFutures){
      switch (randomGenerator.nextInt(4) + 1){
        case 1 -> {
          newRandomisedFutures.add(new Future(future));
        }
        case 2 -> {
          Future newFuture = new Future(future);
          newFuture.setMaintenanceMargin(newFuture.getMaintenanceMargin() + 100);
          newRandomisedFutures.add(newFuture);
        }
        case 3 -> {
          Future newFuture = new Future(future);
          newFuture.setMaintenanceMargin(newFuture.getMaintenanceMargin() - 50);
          newRandomisedFutures.add(newFuture);
        }
        case 4 -> {
          newRandomisedFutures.add(new Future(future));
          newRandomisedFutures.add(new Future(future));
        }
      }
    }
    futureRepository.saveAll(newRandomisedFutures);
  }

}
