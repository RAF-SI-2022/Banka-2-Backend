package com.raf.si.Banka2Backend.bootstrap;

import com.raf.si.Banka2Backend.bootstrap.readers.CurrencyReader;
import com.raf.si.Banka2Backend.exceptions.CurrencyNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.models.mariadb.Exchange;
import com.raf.si.Banka2Backend.models.mariadb.Permission;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.repositories.mariadb.*;
import com.raf.si.Banka2Backend.services.ForexService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
  private final BalanceRepository balanceRepository;

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
      BalanceRepository balanceRepository,
      FutureRepository futureRepository) {
    this.forexService = forexService;
    this.userRepository = userRepository;
    this.permissionRepository = permissionRepository;
    this.currencyRepository = currencyRepository;
    this.inflationRepository = inflationRepository;
    this.passwordEncoder = passwordEncoder;
    this.exchangeRepository = exchangeRepository;
    this.balanceRepository = balanceRepository;
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
      this.loadCurrenciesAndInflationTable();
    }

    // If empty, add exchange markets in db from csv
    // New data introduced in V2_2, if we keep this code devs will not get proper exchanges in db
    //    long numberOfExchanges = this.exchangeRepository.count();
    //    if (numberOfExchanges == 0) {
    //      System.out.println("Added exchange markets");
    //      this.loadExchangeMarkets();
    //    }

    System.out.println("Added exchange markets");
    this.loadExchangeMarkets();
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
    // Add initial 100_000 RSD to admin
    Balance balance1 = this.getInitialAdminBalance(admin);
    List<Balance> balances = new ArrayList<>();
    balances.add(balance1);
    admin.setBalances(balances);
    this.userRepository.save(admin);
    this.balanceRepository.save(balance1);
    System.out.println("Loaded!");
  }

  private Balance getInitialAdminBalance(User admin) {
    Balance balance = new Balance();
    balance.setUser(admin);
    Optional<Currency> rsd = this.currencyRepository.findCurrencyByCurrencyCode("RSD");
    if (rsd.isEmpty()) {
      throw new CurrencyNotFoundException("RSD");
    }
    balance.setCurrency(rsd.get());
    balance.setAmount(100000f);
    return balance;
  }

  private void loadCurrenciesAndInflationTable() throws IOException {
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
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        this.currencyRepository.findCurrencyByCurrencyCode(data[4]).isPresent()
                            ? this.currencyRepository.findCurrencyByCurrencyCode(data[4]).get()
                            : null,
                        data[5],
                        data[6],
                        data[7]))
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

  private void loadFutureTable()
      throws IOException, ParseException { // todo promeni da ucitava sa id
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
    String formattedDate = dateFormat.format(new Date());

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
                        data[4],
                        formattedDate,
                        true))
            .toList();

    futureRepository.saveAll(futures);

    randomiseFutureTableData();
  }

  private void randomiseFutureTableData()
      throws ParseException { // calendar.add(Calendar.MONTH, 1);
    List<Future> allFutures = futureRepository.findAll();
    List<Future> newRandomisedFutures = new ArrayList<>();
    Random randomGenerator = new Random();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
    Calendar calendar = Calendar.getInstance();
    int dateIncreaseAmount = 1;
    int moneyDecreased;

    for (Future future : allFutures) {
      moneyDecreased = future.getMaintenanceMargin();

      for (int i = 0; i <= randomGenerator.nextInt(6) + 4; i++) {
        Future newFuture = new Future(future);
        calendar.setTime(dateFormat.parse(newFuture.getSettlementDate()));
        calendar.add(Calendar.MONTH, dateIncreaseAmount++);
        newFuture.setSettlementDate(dateFormat.format(calendar.getTime()));
        moneyDecreased -= randomGenerator.nextInt(200) + 100;
        if(moneyDecreased<=0){
          moneyDecreased = randomGenerator.nextInt(200) + 100;
        }
        newFuture.setMaintenanceMargin(moneyDecreased);
        newRandomisedFutures.add(newFuture);
      }
      dateIncreaseAmount = 1;
    }

    futureRepository.saveAll(newRandomisedFutures);
  }
}
