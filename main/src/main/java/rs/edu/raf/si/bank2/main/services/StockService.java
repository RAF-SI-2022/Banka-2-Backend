package rs.edu.raf.si.bank2.main.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.si.bank2.main.exceptions.ExchangeNotFoundException;
import rs.edu.raf.si.bank2.main.exceptions.ExternalAPILimitReachedException;
import rs.edu.raf.si.bank2.main.exceptions.OrderNotFoundException;
import rs.edu.raf.si.bank2.main.exceptions.StockNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.ExchangeRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.StockHistoryRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.StockRepository;
import rs.edu.raf.si.bank2.main.requests.StockRequest;
import rs.edu.raf.si.bank2.main.services.workerThreads.StockBuyWorker;
import rs.edu.raf.si.bank2.main.services.workerThreads.StockSellWorker;
import rs.edu.raf.si.bank2.main.services.workerThreads.StocksRetrieverFromApiWorker;

@Service
public class StockService {

    private static final String KEY = "OF6BVKZOCXWHD9NS";
    private static final String ONE_DAY = "ONE_DAY";
    private static final String FIVE_DAYS = "FIVE_DAYS";
    private static final String ONE_MONTH = "ONE_MONTH";
    private static final String SIX_MONTHS = "SIX_MONTHS";
    private static final String ONE_YEAR = "ONE_YEAR";
    private static final String YTD = "YTD";
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String OPEN = "1. open";
    private static final String HIGH = "2. high";
    private static final String LOW = "3. low";
    private static final String CLOSE = "4. close";
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserService userService;
    private final BalanceService balanceService;
    private final UserStockService userStockService;
    private final StockSellWorker stockSellWorker;
    private final StockBuyWorker stockBuyWorker;
    private final OrderRepository orderRepository;
    private final StocksRetrieverFromApiWorker stocksRetrieverFromApiWorker;
    private static final BlockingQueue<StockOrder> stockBuyRequestsQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<StockOrder> stockSellRequestsQueue = new LinkedBlockingQueue<>();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public StockService(
            StockRepository stockRepository,
            StockHistoryRepository stockHistoryRepository,
            UserService userService,
            UserStockService userStockService,
            CurrencyService currencyService,
            TransactionService transactionService,
            ExchangeRepository exchangeRepository,
            BalanceService balanceService,
            OrderRepository orderRepository,
            UserCommunicationService userCommunicationService) {
        this.stockRepository = stockRepository;
        this.exchangeRepository = exchangeRepository;
        this.stockHistoryRepository = stockHistoryRepository;
        this.userService = userService;
        this.userStockService = userStockService;
        this.balanceService = balanceService;
        this.orderRepository = orderRepository;
        this.stockBuyWorker = new StockBuyWorker(
                stockBuyRequestsQueue,
                userStockService,
                this,
                balanceService,
                currencyService,
                transactionService,
                orderRepository,
                userService,
                userCommunicationService);
        this.stockSellWorker = new StockSellWorker(
                stockSellRequestsQueue,
                userStockService,
                this,
                transactionService,
                orderRepository,
                balanceService,
                userCommunicationService);
        stockBuyWorker.start();
        stockSellWorker.start();
        this.stocksRetrieverFromApiWorker = new StocksRetrieverFromApiWorker(this);
        //        this.stocksRetrieverFromApiWorker.start(); TODO VRATI UPDATER
    }

    @Cacheable(value = "stockALL")
    public List<Stock> getAllStocks() {
        System.out.println("Getting all stock - fist time (caching into redis)");
        return stockRepository.findAll();
    }

    @Cacheable(value = "stockID", key = "#id")
    public Stock getStockById(Long id) throws StockNotFoundException {
        Optional<Stock> stockOptional = stockRepository.findById(id);
        System.out.println("Getting stock by id- fist time (caching into redis)");
        if (stockOptional.isPresent()) return stockOptional.get();
        else throw new StockNotFoundException(id);
    }

    public Stock findStockBySymbolInDb(String symbol) {
        return this.stockRepository
                .findStockBySymbol(symbol)
                .orElse(null); // if found return Stock, if not, return null
    }

    @Cacheable(value = "exchangesSymbol", key = "#symbol")
    @Transactional
    public Stock getStockBySymbol(String symbol) throws StockNotFoundException, ExchangeNotFoundException {
        Optional<Stock> stockFromDB = stockRepository.findStockBySymbol(symbol);

        System.out.println("Getting stock by symbol- first time (caching into redis)");

        if (stockFromDB.isPresent()) return stockFromDB.get();
        else {
            HttpClient client = HttpClient.newHttpClient();

            String companyOverviewUrl =
                    "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + symbol + "&apikey=" + KEY;
            String globalQuoteUrl =
                    "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + KEY;
            String websiteLinkUrl =
                    "https://query1.finance.yahoo.com/v11/finance/quoteSummary/" + symbol + "?modules=assetProfile";

            HttpRequest companyOverviewRequest =
                    HttpRequest.newBuilder().uri(URI.create(companyOverviewUrl)).build();
            HttpRequest globalQuoteRequest =
                    HttpRequest.newBuilder().uri(URI.create(globalQuoteUrl)).build();
            HttpRequest websiteLinkRequest =
                    HttpRequest.newBuilder().uri(URI.create(websiteLinkUrl)).build();

            HttpResponse<String> companyOverviewResponse;
            HttpResponse<String> globalQuoteResponse;
            HttpResponse<String> websiteLinkResponse;

            Stock stock;

            try {
                companyOverviewResponse = client.send(companyOverviewRequest, HttpResponse.BodyHandlers.ofString());
                JSONObject companyOverview = new JSONObject(companyOverviewResponse.body());

                globalQuoteResponse = client.send(globalQuoteRequest, HttpResponse.BodyHandlers.ofString());
                JSONObject globalQuoteJson = new JSONObject(globalQuoteResponse.body());
                JSONObject globalQuote = globalQuoteJson.getJSONObject("Global Quote");

                websiteLinkResponse = client.send(websiteLinkRequest, HttpResponse.BodyHandlers.ofString());
                JSONObject websiteLinkJson = new JSONObject(websiteLinkResponse.body());
                JSONObject quoteSummary = websiteLinkJson.getJSONObject("quoteSummary");
                JSONArray result = quoteSummary.getJSONArray("result");
                JSONObject assetProfile = result.getJSONObject(0).getJSONObject("assetProfile");
                String website = assetProfile.getString("website");
                //
                //                Optional<Exchange> exchangeOptional =//todo vrati kad proradi
                //
                // exchangeRepository.findExchangeByAcronym(companyOverview.getString("Exchange"));
                Optional<Exchange> exchangeOptional = exchangeRepository.findExchangeByAcronym("NYSE");

                if (exchangeOptional.isPresent()) {
                    // This method is called from separate thread.
                    // In that case error occurs: "detached entity passed to persist:
                    // rs.edu.raf.si.bank2.main.models.mariadb.Exchange"
                    // To avoid that we check if exchange entity is in detached state. If it is, merge it.
                    Exchange exchange = exchangeOptional.get();
                    //                    if (!entityManager.contains(exchange)) { todo vrati
                    //                        exchange = this.entityManager.merge(exchange);
                    //                    }
                    stock = Stock.builder()
                            .exchange(exchange)
                            .symbol(symbol)
                            .companyName(companyOverview.getString("Name"))
                            .dividendYield(new BigDecimal(companyOverview.getString("DividendYield")))
                            .outstandingShares(companyOverview.getLong("SharesOutstanding"))
                            .openValue(new BigDecimal(globalQuote.getString("02. open")))
                            .highValue(new BigDecimal(globalQuote.getString("03. high")))
                            .lowValue(new BigDecimal(globalQuote.getString("04. low")))
                            .priceValue(new BigDecimal(globalQuote.getString("05. price")))
                            .volumeValue(globalQuote.getLong("06. volume"))
                            .lastUpdated(LocalDate.parse(globalQuote.getString("07. latest trading day")))
                            .previousClose(new BigDecimal(globalQuote.getString("08. previous close")))
                            .changeValue(new BigDecimal(globalQuote.getString("09. change")))
                            .changePercent(globalQuote.getString("10. change percent"))
                            .websiteUrl(website)
                            .build();

                    stockRepository.save(stock);
                } else {
                    throw new ExchangeNotFoundException();
                }

            } catch (IOException | JSONException e) {
                throw new ExternalAPILimitReachedException();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Optional<Stock> savedStock = stockRepository.findStockBySymbol(symbol);

            if (savedStock.isPresent()) return savedStock.get();
            else throw new StockNotFoundException(symbol);
        }
    }

    public List<StockHistory> getStockHistoryByStockIdAndTimePeriod(Long id, String type)
            throws StockNotFoundException {

        Optional<Stock> stockFromDB = stockRepository.findById(id);

        if (stockFromDB.isEmpty()) throw new StockNotFoundException(id);

        if (type.equals(YTD)) return stockHistoryRepository.getStockHistoryByStockIdForYTD(id);

        Integer period = null;

        switch (type) {
            case ONE_YEAR -> period = 365;
            case SIX_MONTHS -> period = 180;
            case ONE_MONTH -> period = 30;
        }

        if (period != null) return stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, period);
        else return stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, type);
    }

    public List<StockHistory> getStockHistoryForStockByIdAndType(Long stockId, String type)
            throws StockNotFoundException, ExternalAPILimitReachedException {

        Stock stock = getStockById(stockId);

        HttpClient client = HttpClient.newHttpClient();

        String url =
                switch (type) {
                    case ONE_DAY -> "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="
                            + stock.getSymbol()
                            + "&interval=5min&outputsize=full&apikey="
                            + KEY;
                    case FIVE_DAYS -> "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="
                            + stock.getSymbol()
                            + "&interval=60min&outputsize=full&apikey="
                            + KEY;
                    case ONE_MONTH,
                            SIX_MONTHS,
                            ONE_YEAR,
                            YTD -> "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="
                            + stock.getSymbol()
                            + "&outputsize=full&apikey="
                            + KEY;
                    default -> null;
                };

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return parseResponse(response, stock, type);
        } catch (IOException e) {
            return getStockHistoryByStockIdAndTimePeriod(stockId, type);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public List<StockHistory> parseResponse(HttpResponse<String> response, Stock stock, String type)
            throws ExternalAPILimitReachedException {

        List<StockHistory> stockHistoryList = new ArrayList<>();

        JSONObject fullResponse = new JSONObject(response.body());
        String key = "";
        int limit = 365;

        switch (type) {
            case ONE_DAY -> key = "Time Series (5min)";
            case FIVE_DAYS -> key = "Time Series (60min)";
            case ONE_MONTH, SIX_MONTHS, ONE_YEAR, YTD -> key = "Time Series (Daily)";
        }

        JSONObject timeSeries;

        try {
            timeSeries = fullResponse.getJSONObject(key);
            //            System.out.println(timeSeries);
        } catch (JSONException e) {
            throw new ExternalAPILimitReachedException();
        }

        Iterator<String> iterator = timeSeries.keys();

        Set<String> timestamps = new HashSet<>();
        while (iterator.hasNext()) {
            timestamps.add(iterator.next());
        }

        List<String> listTimestamps = new ArrayList<>(timestamps);

        Collections.sort(listTimestamps);
        Collections.reverse(listTimestamps);

        switch (type) {
            case ONE_DAY -> {
                LocalDateTime latestTimestamp =
                        LocalDateTime.parse(listTimestamps.get(0), DateTimeFormatter.ofPattern(DATE_PATTERN));

                for (int i = 1; i < listTimestamps.size() - 1; i++) {

                    LocalDateTime localDateTime =
                            LocalDateTime.parse(listTimestamps.get(i), DateTimeFormatter.ofPattern(DATE_PATTERN));

                    JSONObject data = timeSeries.getJSONObject(listTimestamps.get(i));

                    if (latestTimestamp.toLocalDate().equals(localDateTime.toLocalDate())) {

                        StockHistory stockHistory = StockHistory.builder()
                                .openValue(new BigDecimal(data.getString(OPEN)))
                                .highValue(new BigDecimal(data.getString(HIGH)))
                                .lowValue(new BigDecimal(data.getString(LOW)))
                                .closeValue(new BigDecimal(data.getString(CLOSE)))
                                .volumeValue(data.getLong("5. volume"))
                                .stock(stock)
                                .onDate(localDateTime)
                                .type(StockHistoryType.ONE_DAY)
                                .build();

                        stockHistoryList.add(stockHistory);

                        if (localDateTime.toLocalTime().equals(latestTimestamp.toLocalTime())) break;
                    }
                }

                stockHistoryRepository.deleteByStockIdAndType(stock.getId(), StockHistoryType.ONE_DAY);
                stockHistoryRepository.saveAll(stockHistoryList);
            }
            case FIVE_DAYS -> {
                LocalDateTime initiallocalDateTime =
                        LocalDateTime.parse(listTimestamps.get(0), DateTimeFormatter.ofPattern(DATE_PATTERN));

                int dayCounter = 0;

                for (int i = 0; i < listTimestamps.size() - 1; i++) {

                    if (dayCounter == 5 * 16) break;

                    LocalDateTime localDateTime =
                            LocalDateTime.parse(listTimestamps.get(i), DateTimeFormatter.ofPattern(DATE_PATTERN));

                    if (!initiallocalDateTime.toLocalDate().equals(localDateTime.toLocalDate())) dayCounter++;

                    JSONObject data = timeSeries.getJSONObject(listTimestamps.get(i));

                    StockHistory stockHistory = StockHistory.builder()
                            .openValue(new BigDecimal(data.getString(OPEN)))
                            .highValue(new BigDecimal(data.getString(HIGH)))
                            .lowValue(new BigDecimal(data.getString(LOW)))
                            .closeValue(new BigDecimal(data.getString(CLOSE)))
                            .volumeValue(data.getLong("5. volume"))
                            .stock(stock)
                            .onDate(localDateTime)
                            .type(StockHistoryType.FIVE_DAYS)
                            .build();

                    stockHistoryList.add(stockHistory);
                }

                stockHistoryRepository.deleteByStockIdAndType(stock.getId(), StockHistoryType.FIVE_DAYS);
                stockHistoryRepository.saveAll(stockHistoryList);
            }
            case ONE_MONTH, SIX_MONTHS, ONE_YEAR, YTD -> {
                for (int i = 0; i < limit; i++) {

                    LocalDateTime localDateTime = LocalDateTime.parse(
                            listTimestamps.get(i) + " 00:00:00", DateTimeFormatter.ofPattern(DATE_PATTERN));

                    JSONObject data = timeSeries.getJSONObject(listTimestamps.get(i));

                    StockHistory stockHistory = StockHistory.builder()
                            .openValue(new BigDecimal(data.getString(OPEN)))
                            .highValue(new BigDecimal(data.getString(HIGH)))
                            .lowValue(new BigDecimal(data.getString(LOW)))
                            .closeValue(new BigDecimal(data.getString(CLOSE)))
                            .volumeValue(data.getLong("6. volume"))
                            .stock(stock)
                            .onDate(localDateTime)
                            .type(StockHistoryType.DAILY)
                            .build();

                    stockHistoryList.add(stockHistory);
                }

                stockHistoryRepository.deleteByStockIdAndType(stock.getId(), StockHistoryType.DAILY);
                stockHistoryRepository.saveAll(stockHistoryList);
            }
        }

        Collections.reverse(stockHistoryList);

        return switch (type) {
            case ONE_MONTH -> stockHistoryList.subList(0, 30);
            case SIX_MONTHS -> stockHistoryList.subList(0, 180);
            case YTD -> {
                List<StockHistory> stockHistoryListYTD = new ArrayList<>();

                int yearToLookUp = stockHistoryList
                        .get(stockHistoryList.size() - 1)
                        .getOnDate()
                        .getYear();

                for (StockHistory sh : stockHistoryList) {

                    if (yearToLookUp == sh.getOnDate().getYear()) stockHistoryListYTD.add(sh);
                }

                yield stockHistoryListYTD;
            }
            default -> stockHistoryList;
        };
    }

    // todo margin buy i sell
    public ResponseEntity<?> buyStock(StockRequest stockRequest, User user, StockOrder stockOrder, boolean approved) {
        Stock stock = getStockBySymbol(stockRequest.getStockSymbol());
        BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));

        StockOrder order;
        if (user.getDailyLimit() == null) { // || user.getDailyLimit() <= 0
            return ResponseEntity.internalServerError().body("Doslo je do neocekivane greske.");
        }

        // napravimo order is stavimo na cekanje ako ne moze da prodje
        if (!approved && price.doubleValue() > user.getDailyLimit()) {
            order = stockOrder == null
                    ? this.createOrder(stockRequest, price.doubleValue(), user, OrderStatus.WAITING, OrderTradeType.BUY)
                    : stockOrder;
            this.orderRepository.save(order);
            return ResponseEntity.status(400).body("Dnevni limit je prekoracen, porudzbina je na cekanju (WAITING).");
        }

        order = stockOrder == null
                ? this.createOrder(stockRequest, price.doubleValue(), user, OrderStatus.IN_PROGRESS, OrderTradeType.BUY)
                : stockOrder;
        order = this.orderRepository.save(order);

        // ako je MARGIN order posalji ga na drugi service umesto da ga ovde obradjujes
        //        if (order.isMargin()){
        //            CommunicationDto communicationDto;
        //            MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
        //            marginTransactionDto.setAccountType(AccountType.MARGIN);
        //            marginTransactionDto.setOrderId(order.getId());
        //            marginTransactionDto.setTransactionComment("Kupovina akcija");
        //            marginTransactionDto.setCurrencyCode(order.getCurrencyCode());
        //            marginTransactionDto.setTransactionType(TransactionType.BUY);
        //            marginTransactionDto.setInitialMargin(price.doubleValue());
        //            marginTransactionDto.setMaintenanceMargin(price.doubleValue() * 0.4); //za odrzavanje je 40% full
        // cene
        //            try {
        //                String marginDtoJson = mapper.writeValueAsString(marginTransactionDto);
        //                communicationDto = userCommunicationInterface.sendMarginTransaction("/makeTransaction",
        // marginDtoJson, user.getEmail());
        //            } catch (JsonProcessingException e) { throw new RuntimeException(e); }
        //
        //            if (communicationDto.getResponseCode() == 200)
        //                this.updateOrderStatus(order.getId(), OrderStatus.COMPLETE);
        //            return
        // ResponseEntity.status(communicationDto.getResponseCode()).body(communicationDto.getResponseMsg());
        //        }

        try {
            stockBuyRequestsQueue.put(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Balance usersBalance = balanceService.findBalanceByUserIdAndCurrency(user.getId(), order.getCurrencyCode());
        if (!approved && usersBalance.getAmount() < price.doubleValue()) {
            return ResponseEntity.status(400).body("Nemate dovoljno novca za ovu operaciju.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Kupovina akcije je uspesna.");
        return ResponseEntity.ok().body(response);
    }

    private void updateOrderStatus(Long id, OrderStatus orderStatus) {
        Optional<Order> order = this.orderRepository.findById(id);

        if (order.isPresent()) {
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        } else throw new OrderNotFoundException(id);
    }

    private StockOrder createOrder(
            StockRequest request, Double price, User user, OrderStatus status, OrderTradeType orderTradeType) {
        return new StockOrder(
                0L,
                OrderType.STOCK,
                orderTradeType,
                status,
                request.getStockSymbol(),
                request.getAmount(),
                price,
                this.getTimestamp(),
                user,
                request.getLimit(),
                request.getStop(),
                request.isAllOrNone(),
                request.isMargin(),
                request.getCurrencyCode());
    }

    private String getTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return currentDateTime.format(formatter);
    }

    public ResponseEntity<?> sellStock(StockRequest stockRequest, StockOrder stockOrder) {

        Optional<UserStock> userStock = userStockService.findUserStockByUserIdAndStockSymbol(
                stockRequest.getUserId(), stockRequest.getStockSymbol());
        BigDecimal price =
                userStock.get().getStock().getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));

        if (userStock.get().getAmount() < stockRequest.getAmount()) {
            return ResponseEntity.status(400).body("Nemate dovoljno hartija za ovu operaciju.");
        }

        try {
            StockOrder order = stockOrder == null
                    ? this.createOrder(
                            stockRequest,
                            price.doubleValue(),
                            userStock.get().getUser(),
                            OrderStatus.WAITING,
                            OrderTradeType.SELL)
                    : stockOrder;
            order = this.orderRepository.save(order);

            //            //ako je MARGIN order posalji ga na drugi service umesto da ga ovde obradjujes
            //            if (order.isMargin()){//todo proveri dal radi
            //                CommunicationDto communicationDto;
            //                MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
            //                marginTransactionDto.setAccountType(AccountType.MARGIN);
            //                marginTransactionDto.setOrderId(order.getId());
            //                marginTransactionDto.setTransactionComment("Prodaja akcija");
            //                marginTransactionDto.setCurrencyCode(order.getCurrencyCode());
            //                marginTransactionDto.setTransactionType(TransactionType.SELL);
            //                marginTransactionDto.setInitialMargin(price.doubleValue());
            //                marginTransactionDto.setMaintenanceMargin(price.doubleValue() * 0.4); //za odrzavanje je
            // 40% full cene
            //                try {
            //                    String marginDtoJson = mapper.writeValueAsString(marginTransactionDto);
            //                    communicationDto =
            // userCommunicationInterface.sendMarginTransaction("/makeTransaction", marginDtoJson,
            // userStock.get().getUser().getEmail());
            //                } catch (JsonProcessingException e) { throw new RuntimeException(e); }
            //
            //                if (communicationDto.getResponseCode() == 200)
            //                    this.updateOrderStatus(order.getId(), OrderStatus.COMPLETE);
            //                return
            // ResponseEntity.status(communicationDto.getResponseCode()).body(communicationDto.getResponseMsg());
            //            }

            stockSellRequestsQueue.put(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Akcija je uspesno postavljena na prodaju");
        return ResponseEntity.ok().body(response);
    }

    public List<UserStock> getAllUserStocks(long userId) {
        return userStockService.findAllForUser(userId);
    }

    public boolean checkLimitAndStopForBuy(StockOrder stockOrder) {
        // Here we have to find price of the stock referenced in stockOrder and to compare it with order's limit and
        // stop
        BigDecimal stockPrice =
                this.findStockBySymbolInDb(stockOrder.getSymbol()).getPriceValue();
        if (stockOrder.getStockLimit() != null
                && stockPrice.doubleValue() <= stockOrder.getStockLimit().doubleValue()) {
            return true;
        }
        return stockOrder.getStop() != null
                && stockPrice.doubleValue() >= stockOrder.getStop().doubleValue();
    }

    public boolean checkLimitAndStopForSell(StockOrder stockOrder) {
        BigDecimal stockPrice =
                this.findStockBySymbolInDb(stockOrder.getSymbol()).getPriceValue();
        if (stockOrder.getStockLimit() != null
                && stockPrice.doubleValue() >= stockOrder.getStockLimit().doubleValue()) {
            return true;
        }
        return stockOrder.getStop() != null
                && stockPrice.doubleValue() <= stockOrder.getStop().doubleValue();
    }

    @Transactional
    public List<Stock> getFromExternalApi() {
        Stock appleStock = this.getStockBySymbol("AAPL");
        Stock googleStock = this.getStockBySymbol("GOOGL");
        Stock amazonStock = this.getStockBySymbol("AMZN");
        Stock teslaStock = this.getStockBySymbol("TSLA");
        Stock netflixStock = this.getStockBySymbol("NFLX");

        List<Stock> stockList = new ArrayList<>();
        stockList.add(appleStock);
        stockList.add(googleStock);
        stockList.add(amazonStock);
        stockList.add(teslaStock);
        stockList.add(netflixStock);
        return stockList;
    }

    @Transactional
    public void updateAllStocksInDb() {
        List<Stock> freshStocks = this.getFromExternalApi();
        for (Stock freshStock : freshStocks) {
            Optional<Stock> existingStock = this.stockRepository.findStockBySymbol(freshStock.getSymbol());
            if (existingStock.isPresent()) {
                this.stockRepository
                        .updateStock( // forwarding all fields as arguments because forwarding whole object was
                                // problematic
                                freshStock.getSymbol(),
                                freshStock.getCompanyName(),
                                freshStock.getOutstandingShares(),
                                freshStock.getDividendYield(),
                                freshStock.getPriceValue(),
                                freshStock.getOpenValue(),
                                freshStock.getLowValue(),
                                freshStock.getHighValue(),
                                freshStock.getChangeValue(),
                                freshStock.getPreviousClose(),
                                freshStock.getVolumeValue(),
                                freshStock.getLastUpdated(),
                                freshStock.getChangePercent(),
                                freshStock.getWebsiteUrl());
            } else {
                this.stockRepository.save(freshStock);
            }
        }
    }
}
