package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.ExchangeNotFoundException;
import com.raf.si.Banka2Backend.exceptions.ExternalAPILimitReachedException;
import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.models.mariadb.orders.*;
import com.raf.si.Banka2Backend.repositories.mariadb.ExchangeRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.StockHistoryRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.StockRepository;
import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.workerThreads.StockBuyWorker;
import com.raf.si.Banka2Backend.services.workerThreads.StockSellWorker;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private static final String KEY = "J3WDDK9HZ1G71YIP";
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserService userService;
    private final BalanceService balanceService;
    private final UserStockService userStockService;
    private final StockSellWorker stockSellWorker;
    private final StockBuyWorker stockBuyWorker;

    private final OrderRepository orderRepository;

    private CurrencyService currencyService;
    private TransactionService transactionService;

    private static final BlockingQueue<StockOrder> stockBuyRequestsQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<StockOrder> stockSellRequestsQueue = new LinkedBlockingQueue<>();

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
            OrderRepository orderRepository) {
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
                orderRepository);
        this.stockSellWorker = new StockSellWorker(
                stockSellRequestsQueue, userStockService, this, transactionService, orderRepository, balanceService);
        stockBuyWorker.start();
        stockSellWorker.start();
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockById(Long id) throws StockNotFoundException {

        Optional<Stock> stockOptional = stockRepository.findById(id);

        if (stockOptional.isPresent()) return stockOptional.get();
        else throw new StockNotFoundException(id);
    }

    public Stock getStockBySymbol(String symbol) throws StockNotFoundException, ExchangeNotFoundException {

        Optional<Stock> stockFromDB = stockRepository.findStockBySymbol(symbol);

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

                if (companyOverview.isEmpty() || globalQuoteJson.isEmpty() || websiteLinkJson.isEmpty())
                    throw new StockNotFoundException(symbol);

                Optional<Exchange> exchange =
                        exchangeRepository.findExchangeByAcronym(companyOverview.getString("Exchange"));

                if (exchange.isPresent()) {
                    stock = Stock.builder()
                            .exchange(exchange.get())
                            .symbol(symbol)
                            .companyName(companyOverview.getString("Name"))
                            .dividendYield(companyOverview.getBigDecimal("DividendYield"))
                            .outstandingShares(companyOverview.getLong("SharesOutstanding"))
                            .openValue(globalQuote.getBigDecimal("02. open"))
                            .highValue(globalQuote.getBigDecimal("03. high"))
                            .lowValue(globalQuote.getBigDecimal("04. low"))
                            .priceValue(globalQuote.getBigDecimal("05. price"))
                            .volumeValue(globalQuote.getLong("06. volume"))
                            .lastUpdated(LocalDate.parse(globalQuote.getString("07. latest trading day")))
                            .previousClose(globalQuote.getBigDecimal("08. previous close"))
                            .changeValue(globalQuote.getBigDecimal("09. change"))
                            .changePercent(globalQuote.getString("10. change percent"))
                            .websiteUrl(website)
                            .build();

                    stockRepository.save(stock);
                } else {
                    throw new ExchangeNotFoundException();
                }

            } catch (IOException | JSONException e) {
                throw new StockNotFoundException(symbol);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

        if (type.equals("YTD")) return stockHistoryRepository.getStockHistoryByStockIdForYTD(id);

        Integer period = null;

        switch (type) {
            case "ONE_YEAR" -> period = 365;
            case "SIX_MONTHS" -> period = 180;
            case "ONE_MONTH" -> period = 30;
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
                    case "ONE_DAY" -> "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="
                            + stock.getSymbol()
                            + "&interval=5min&outputsize=full&apikey="
                            + KEY;
                    case "FIVE_DAYS" -> "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="
                            + stock.getSymbol()
                            + "&interval=60min&outputsize=full&apikey="
                            + KEY;
                    case "ONE_MONTH",
                            "SIX_MONTHS",
                            "ONE_YEAR",
                            "YTD" -> "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="
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
            throw new RuntimeException(e);
        }
    }

    public List<StockHistory> parseResponse(HttpResponse<String> response, Stock stock, String type)
            throws ExternalAPILimitReachedException {

        List<StockHistory> stockHistoryList = new ArrayList<>();

        JSONObject fullResponse = new JSONObject(response.body());
        String key = "";
        int limit = 365;

        switch (type) {
            case "ONE_DAY" -> key = "Time Series (5min)";
            case "FIVE_DAYS" -> key = "Time Series (60min)";
            case "ONE_MONTH", "SIX_MONTHS", "ONE_YEAR", "YTD" -> key = "Time Series (Daily)";
        }

        JSONObject timeSeries;

        try {
            timeSeries = fullResponse.getJSONObject(key);
        } catch (JSONException e) {
            throw new ExternalAPILimitReachedException();
        }

        Set<String> timestamps = timeSeries.keySet();
        List<String> listTimestamps = new ArrayList<>(timestamps);

        Collections.sort(listTimestamps);
        Collections.reverse(listTimestamps);

        switch (type) {
            case "ONE_DAY" -> {
                LocalDateTime latestTimestamp =
                        LocalDateTime.parse(listTimestamps.get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                for (int i = 1; i < listTimestamps.size() - 1; i++) {

                    LocalDateTime localDateTime = LocalDateTime.parse(
                            listTimestamps.get(i), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    JSONObject data = timeSeries.getJSONObject(listTimestamps.get(i));

                    if (latestTimestamp.toLocalDate().equals(localDateTime.toLocalDate())) {

                        StockHistory stockHistory = StockHistory.builder()
                                .openValue(data.getBigDecimal("1. open"))
                                .highValue(data.getBigDecimal("2. high"))
                                .lowValue(data.getBigDecimal("3. low"))
                                .closeValue(data.getBigDecimal("4. close"))
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
            case "FIVE_DAYS" -> {
                LocalDateTime initiallocalDateTime =
                        LocalDateTime.parse(listTimestamps.get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                int dayCounter = 0;

                for (int i = 0; i < listTimestamps.size() - 1; i++) {

                    if (dayCounter == 5 * 16) break;

                    LocalDateTime localDateTime = LocalDateTime.parse(
                            listTimestamps.get(i), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    if (!initiallocalDateTime.toLocalDate().equals(localDateTime.toLocalDate())) dayCounter++;

                    JSONObject data = timeSeries.getJSONObject(listTimestamps.get(i));

                    StockHistory stockHistory = StockHistory.builder()
                            .openValue(data.getBigDecimal("1. open"))
                            .highValue(data.getBigDecimal("2. high"))
                            .lowValue(data.getBigDecimal("3. low"))
                            .closeValue(data.getBigDecimal("4. close"))
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
            case "ONE_MONTH", "SIX_MONTHS", "ONE_YEAR", "YTD" -> {
                for (int i = 0; i < limit; i++) {

                    LocalDateTime localDateTime = LocalDateTime.parse(
                            listTimestamps.get(i) + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    JSONObject data = timeSeries.getJSONObject(listTimestamps.get(i));

                    StockHistory stockHistory = StockHistory.builder()
                            .openValue(data.getBigDecimal("1. open"))
                            .highValue(data.getBigDecimal("2. high"))
                            .lowValue(data.getBigDecimal("3. low"))
                            .closeValue(data.getBigDecimal("4. close"))
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
            case "ONE_MONTH" -> stockHistoryList.subList(0, 30);
            case "SIX_MONTHS" -> stockHistoryList.subList(0, 180);
            case "YTD" -> {
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
    public ResponseEntity<?> buyStock(StockRequest stockRequest, User user, StockOrder stockOrder) {
        Stock stock = getStockBySymbol(stockRequest.getStockSymbol());
        BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));
        Balance usersBalance =
                balanceService.findBalanceByUserIdAndCurrency(user.getId(), stockRequest.getCurrencyCode());

        StockOrder order = null;
        if (user.getDailyLimit() == null || user.getDailyLimit() <= 0) {
            return ResponseEntity.internalServerError().body("User daily limit is not present.");
        }
        //        System.out.println("check");
        //        System.out.println(price.doubleValue());
        //        System.out.println(user.getDailyLimit());

        if (price.doubleValue() > user.getDailyLimit()) {
            order = stockOrder == null
                    ? this.createOrder(stockRequest, price.doubleValue(), user, OrderStatus.WAITING, OrderTradeType.BUY)
                    : stockOrder;
            this.orderRepository.save(order);
            return ResponseEntity.status(202).body("Daily limit exceeded, order is in status WAITING.");
        } else {
            order = stockOrder == null
                    ? this.createOrder(
                            stockRequest, price.doubleValue(), user, OrderStatus.IN_PROGRESS, OrderTradeType.BUY)
                    : stockOrder;
            order = (StockOrder) this.orderRepository.save(order);
            user.setDailyLimit(user.getDailyLimit() - price.doubleValue());
            userService.save(user);
        }
        this.balanceService.reserveAmount(price.floatValue(), user.getEmail(), stockRequest.getCurrencyCode());
        try {
            stockBuyRequestsQueue.put(order);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body("Stock buy order has been processed");
    }

    private StockOrder createOrder(
            StockRequest request, Double price, User user, OrderStatus status, OrderTradeType orderTradeType) {
        StockOrder order = new StockOrder(
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
        return order;
    }

    private String getTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    public ResponseEntity<?> sellStock(StockRequest stockRequest, StockOrder stockOrder) {

        Optional<UserStock> userStock = userStockService.findUserStockByUserIdAndStockSymbol(
                stockRequest.getUserId(), stockRequest.getStockSymbol());
        BigDecimal price =
                userStock.get().getStock().getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));
        if (userStock.get().getAmount() < stockRequest.getAmount()) {
            return ResponseEntity.status(400).body("Insufficient funds for this operation.");
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
            order = (StockOrder) this.orderRepository.save(order);
            stockSellRequestsQueue.put(order);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body("Stock sell order has been processed");
    }

    public List<UserStock> getAllUserStocks(long userId) {
        return userStockService.findAllForUser(userId);
    }
}
