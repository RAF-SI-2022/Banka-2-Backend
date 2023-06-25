package rs.edu.raf.si.bank2.main.bootstrap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.main.bootstrap.readers.CSVReader;
import rs.edu.raf.si.bank2.main.bootstrap.readers.CurrencyReader;
import rs.edu.raf.si.bank2.main.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.Currency;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;

import javax.persistence.EntityManagerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Component
public class BootstrapData implements CommandLineRunner {

    public static final String forexApiKey = "OF6BVKZOCXWHD9NS";

    private final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);

    private final CurrencyRepository currencyRepository;
    private final InflationRepository inflationRepository;
    private final ExchangeRepository exchangeRepository;
    private final FutureRepository futureRepository;
    private final BalanceRepository balanceRepository;
    private final StockRepository stockRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;

    @Autowired
    public BootstrapData(
            CurrencyRepository currencyRepository,
            InflationRepository inflationRepository,
            ExchangeRepository exchangeRepository,
            FutureRepository futureRepository,
            BalanceRepository balanceRepository,
            StockRepository stockRepository,
            EntityManagerFactory entityManagerFactory, UserRepository userRepository) {
        this.currencyRepository = currencyRepository;
        this.inflationRepository = inflationRepository;
        this.exchangeRepository = exchangeRepository;
        this.futureRepository = futureRepository;
        this.balanceRepository = balanceRepository;
        this.stockRepository = stockRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // If empty, add futures in db from csv
        if (this.futureRepository.count() == 0) {
            this.loadFutureTable();
            logger.info("Added futures");
        }

        // If empty, add currencies in db from csv
        if (this.currencyRepository.count() == 0) {
            this.loadCurrenciesAndInflationTable();
            logger.info("Added currencies");
        }

        // If empty, add exchange markets in db from csv
        if (this.exchangeRepository.count() == 0) {
            this.loadExchangeMarkets();
            logger.info("Added exchange markets");
        }

        // If empty, add stocks in db from csv
        if (stockRepository.count() == 0) {
            loadStocksTable();
            logger.info("Added stocks");
        }

        Optional<User> adminUser = userRepository.findUserByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        if (adminUser.isPresent() && balanceRepository.findAllByUser_Id(adminUser.get().getId()).size() == 0) {
            addBalancesToAdmin();
        }


        logger.info("BootstrapData finished adding data!");
    }

    /**
     * Populates the currencies and inflations tables. TODO expand docs
     *
     * @throws IOException
     */
    private void loadCurrenciesAndInflationTable() throws IOException {
        CurrencyReader cs = new CurrencyReader();
        List<Currency> currencyList = cs.getCurrencies();
        this.currencyRepository.saveAll(currencyList);
        List<Inflation> inflationList = cs.getInflations();
        this.inflationRepository.saveAll(inflationList);
    }


    private void addBalancesToAdmin() {
        // Add initial 100_000 RSD to admin
        Optional<User> adminUser = userRepository.findUserByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        User admin = adminUser.get();
        Balance balance1 = this.getInitialAdminBalance(admin, "RSD");
        Balance balance2 = this.getInitialAdminBalance(admin, "USD");
        List<Balance> balances = new ArrayList<>();
        balances.add(balance1);
        balances.add(balance2);
        admin.setBalances(balances);
        this.userRepository.save(admin);
        this.balanceRepository.save(balance1);
        this.balanceRepository.save(balance2);
    }

    private Balance getInitialAdminBalance(User admin, String currency) {
        Balance balance = new Balance();
        balance.setUser(admin);
        Optional<rs.edu.raf.si.bank2.main.models.mariadb.Currency> curr =
                this.currencyRepository.findCurrencyByCurrencyCode(currency);
        if (curr.isEmpty()) throw new CurrencyNotFoundException(currency);
        balance.setCurrency(curr.get());
        balance.setAmount(100000f);
        balance.setFree(100000f);
        balance.setReserved(0f);
        balance.setType(BalanceType.CASH);
        return balance;
    }

    /**
     * Populates the exchange markets table. TODO expand docs
     */
    private void loadExchangeMarkets() {
        // Do this only on the first ever run of the app.
        // read from file

        String resPath = "/csvs/exchange_0.csv";
        Optional<Stream<String>> content = CSVReader.getInstance().readCSVStream(resPath);
        if (content.isEmpty()) {
            logger.error("Failed to load CSV " + resPath);
            return;
        }

        List<Exchange> exchanges = content.get()
                .parallel()
                .skip(1)
                .map(line -> line.split(","))
                .filter(data ->
                        exchangeRepository.findExchangeByMicCode(data[2]).isEmpty())
                .map(data -> new Exchange(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        this.currencyRepository
                                .findCurrencyByCurrencyCode(data[4])
                                .isPresent()
                                ? this.currencyRepository
                                .findCurrencyByCurrencyCode(data[4])
                                .get()
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
                if (Objects.equals(e.getAcronym(), e1.getAcronym())) {
                    logger.info("id " + e.getId() + "id " + e1.getId());
                }
            }
        }
        exchangeRepository.saveAll(exchanges);
    }

    /**
     * Populates the futures table. TODO expand docs
     *
     * @throws ParseException
     */
    private void loadFutureTable() throws ParseException { //
        // todo promeni da ucitava sa id
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
        String formattedDate = dateFormat.format(new Date());

        String resPath = "/csvs/future_data.csv";
        Optional<Stream<String>> content = CSVReader.getInstance().readCSVStream(resPath);
        if (content.isEmpty()) {
            logger.error("Failed to load CSV " + resPath);
            return;
        }

        List<Future> futures = content.get()
                .parallel()
                .skip(1)
                .map(line -> line.split(","))
                .filter(data -> futureRepository.findFutureByFutureName(data[0]).isEmpty())
                .map(data -> new Future(
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

    /**
     * Adds randomness to futures. TODO expand docs
     *
     * @throws ParseException
     */
    private void randomiseFutureTableData() throws ParseException { //
        // calendar.add(Calendar.MONTH, 1);
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
                if (moneyDecreased <= 0) {
                    moneyDecreased = randomGenerator.nextInt(200) + 100;
                }
                newFuture.setMaintenanceMargin(moneyDecreased);
                newRandomisedFutures.add(newFuture);
            }
            dateIncreaseAmount = 1;
        }

        futureRepository.saveAll(newRandomisedFutures);
    }

    /**
     * Populates the stocks table. TODO expand docs
     *
     * @throws IOException
     */
    private void loadStocksTable() throws IOException {

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();

        String resPath = "/csvs/stocks.csv";
        Optional<String> content = CSVReader.getInstance().readCSVString(resPath);
        if (content.isEmpty()) {
            logger.error("Failed to load CSV " + resPath);
            return;
        }

        String inp = content.get();
        Reader read = new StringReader(inp);
        BufferedReader br = new BufferedReader(read);

        String header = br.readLine();
        String line = br.readLine();

        while (line != null) {

            String[] data = line.split(",");

            Optional<Exchange> exchange = exchangeRepository.findExchangeByAcronym(data[12]);
            Exchange mergedExchange = (Exchange) session.merge(exchange.get());

            Stock stock = Stock.builder()
                    .companyName(data[0])
                    .outstandingShares(Long.valueOf(data[1]))
                    .dividendYield(new BigDecimal(data[2]))
                    .openValue(new BigDecimal(data[3]))
                    .highValue(new BigDecimal(data[4]))
                    .lowValue(new BigDecimal(data[5]))
                    .priceValue(new BigDecimal(data[6]))
                    .volumeValue(Long.valueOf(data[7]))
                    .lastUpdated(LocalDate.now())
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

            resPath = "/csvs/stock_history.csv";
            Optional<String> content1 = CSVReader.getInstance().readCSVString(resPath);
            if (content1.isEmpty()) {
                logger.error("Failed to load CSV " + resPath);
                return;
            }

            // TODO this should be rewritten, resources cannot be accessed as
            //  "normal" files in a jar file. Hacky solution to get it working
            //  with CSVReader

            String inp1 = content1.get();
            Reader read1 = new StringReader(inp1);
            BufferedReader br1 = new BufferedReader(read1);

            Stock mergedStock = (Stock) session.merge(s);

            String header1 = br1.readLine();
            String line1 = br1.readLine();

            while (line1 != null) {

                String[] data = line1.split(",");
                String symbol = data[6];

                if (symbol.equals(s.getSymbol())) {
                    StockHistory stockHistory = StockHistory.builder()
                            .openValue(new BigDecimal(data[0]))
                            .highValue(new BigDecimal(data[1]))
                            .lowValue(new BigDecimal(data[2]))
                            .closeValue(new BigDecimal(data[3]))
                            .volumeValue(Long.valueOf(data[4]))
                            .onDate(
                                    data[5].contains(" ")
                                            ? LocalDateTime.parse(
                                            data[5], DateTimeFormatter.ofPattern("yyyy" + "-MM-dd HH:mm:ss"))
                                            : LocalDateTime.parse(
                                            data[5] + " 00:00:00",
                                            DateTimeFormatter.ofPattern("yyyy" + "-MM-dd HH:mm:ss")))
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
