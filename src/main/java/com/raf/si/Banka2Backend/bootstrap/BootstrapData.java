package com.raf.si.Banka2Backend.bootstrap;

import com.raf.si.Banka2Backend.bootstrap.readers.CurrencyReader;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.models.mariadb.Exchange;
import com.raf.si.Banka2Backend.models.mariadb.Permission;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.repositories.mariadb.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
  private final CurrencyRepository currencyRepository;
  private final InflationRepository inflationRepository;
  private final PasswordEncoder passwordEncoder;
  private final ExchangeRepository exchangeRepository;
  private final FutureRepository futureRepository;
  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;

  private final EntityManagerFactory entityManagerFactory;

  @Autowired
  public BootstrapData(
      UserRepository userRepository,
      PermissionRepository permissionRepository,
      CurrencyRepository currencyRepository,
      InflationRepository inflationRepository,
      PasswordEncoder passwordEncoder,
      ExchangeRepository exchangeRepository,
      FutureRepository futureRepository,
      StockRepository stockRepository,
      StockHistoryRepository stockHistoryRepository,
      EntityManagerFactory entityManagerFactory) {
    this.userRepository = userRepository;
    this.permissionRepository = permissionRepository;
    this.currencyRepository = currencyRepository;
    this.inflationRepository = inflationRepository;
    this.passwordEncoder = passwordEncoder;
    this.exchangeRepository = exchangeRepository;
    this.futureRepository = futureRepository;
    this.stockRepository = stockRepository;
    this.stockHistoryRepository = stockHistoryRepository;
    this.entityManagerFactory = entityManagerFactory;
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

    long numberOfStocks = stockRepository.count();
    if (numberOfStocks == 0) {
      System.out.println("Added stocks");
      loadStocksTable();
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
        Files.lines(Paths.get("src/main/resources/exchange.csv"))
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
        Files.lines(Paths.get("src/main/resources/future_data.csv"))
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
    // todo randomize futures if we want more diversity in futures, also maybe make some of thema
    // lready signed
  }

  private void loadStocksTable() throws IOException {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();

    BufferedReader br = new BufferedReader(new FileReader("src/main/resources/stocks.csv"));

    String header = br.readLine();
    String line = br.readLine();

    while (line != null) {

      String[] data = line.split(",");

      Optional<Exchange> exchange = exchangeRepository.findExchangeByAcronym(data[12]);
      Exchange mergedExchange = (Exchange) session.merge(exchange.get());

      Stock stock =
          Stock.builder()
              .companyName(data[0])
              .outstandingShares(Long.valueOf(data[1]))
              .dividendYield(new BigDecimal(data[2]))
              .openValue(new BigDecimal(data[3]))
              .highValue(new BigDecimal(data[4]))
              .lowValue(new BigDecimal(data[5]))
              .priceValue(new BigDecimal(data[6]))
              .volumeValue(Long.valueOf(data[7]))
              .lastUpdated(LocalDate.parse(data[8]))
              .previousClose(new BigDecimal(data[9]))
              .changeValue(new BigDecimal(data[10]))
              .changePercent(data[11])
              .exchange(mergedExchange)
              .symbol(data[13])
              .websiteUrl(data[14])
              .build();

      session.beginTransaction();
      session.persist(stock);
      session.getTransaction().commit();

      line = br.readLine();
    }

    br.close();

    for (Stock s : stockRepository.findAll()) {

      Stock mergedStock = (Stock) session.merge(s);
      BufferedReader br1 =
          new BufferedReader(new FileReader("src/main/resources/stock_history.csv"));

      String header1 = br1.readLine();
      String line1 = br1.readLine();

      while (line1 != null) {

        String[] data = line1.split(",");
        String symbol = data[6];

        if (symbol.equals(s.getSymbol())) {
          StockHistory stockHistory =
              StockHistory.builder()
                  .openValue(new BigDecimal(data[0]))
                  .highValue(new BigDecimal(data[1]))
                  .lowValue(new BigDecimal(data[2]))
                  .closeValue(new BigDecimal(data[3]))
                  .volumeValue(Long.valueOf(data[4]))
                  .onDate(
                      data[5].contains(" ")
                          ? LocalDateTime.parse(
                              data[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                          : LocalDateTime.parse(
                              data[5] + " 00:00:00",
                              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                  .stock(mergedStock)
                  .type(StockHistoryType.valueOf(data[7]))
                  .build();

          session.beginTransaction();
          session.persist(stockHistory);
          session.getTransaction().commit();
        }
        line1 = br1.readLine();
      }

      br1.close();
    }
  }
}
