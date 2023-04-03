package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.models.mariadb.StockHistory;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import com.raf.si.Banka2Backend.repositories.mariadb.StockHistoryRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.StockRepository;
import com.raf.si.Banka2Backend.requests.StockRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StockService {

  private static final String KEY = "J3WDDK9HZ1G71YIP";
  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final UserService userService;
  private final UserStockService userStockService;
  private static boolean gotAll = false;


  @Autowired
  public StockService(
          StockRepository stockRepository,
          StockHistoryRepository stockHistoryRepository,
          UserService userService, UserStockService userStockService) {
    this.stockRepository = stockRepository;
    this.stockHistoryRepository = stockHistoryRepository;
    this.userService = userService;
    this.userStockService = userStockService;
  }

  public List<Stock> getAllStocks() {
    return stockRepository.findAll();
  }

  public Stock getStockById(Long id) throws StockNotFoundException {

    Optional<Stock> stockOptional = stockRepository.findById(id);

    if (stockOptional.isPresent()) return stockOptional.get();
    else throw new StockNotFoundException(id);
  }

  public Stock getStockBySymbol(String symbol) throws StockNotFoundException {
    HttpClient client = HttpClient.newHttpClient();

    String url =
        "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
            + symbol
            + "&apikey="
            + KEY;
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());

      JSONObject fullResponse = new JSONObject(response.body());
      JSONObject globalQuote = fullResponse.getJSONObject("Global Quote");

      Optional<Stock> stockFromDB = stockRepository.findStockBySymbol(symbol);

      if (stockFromDB.isPresent()) {

        Stock stock =
            Stock.builder()
                .id(stockFromDB.get().getId())
                .symbol(symbol)
                .companyName(stockFromDB.get().getCompanyName())
                .outstandingShares(stockFromDB.get().getOutstandingShares())
                .dividendYield(stockFromDB.get().getDividendYield())
                .priceValue(globalQuote.getBigDecimal("05. price"))
                .openValue(globalQuote.getBigDecimal("02. open"))
                .highValue(globalQuote.getBigDecimal("03. high"))
                .lowValue(globalQuote.getBigDecimal("04. low"))
                .volumeValue(globalQuote.getLong("06. volume"))
                .lastUpdated(LocalDate.parse(globalQuote.getString("07. latest trading day")))
                .previousClose(globalQuote.getBigDecimal("08. previous close"))
                .changeValue(globalQuote.getBigDecimal("09. change"))
                .changePercent(globalQuote.getString("10. change percent"))
                .build();

        return stock;
      } else throw new StockNotFoundException(symbol);

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public List<StockHistory> getStockHistoryByStockId(Long id) {
    return stockHistoryRepository.getStockHistoryByStockId(id);
  }

  public List<StockHistory> getStockHistoryByStockIdAndTimePeriod(Long id, String type) {

    if (type.equals("YTD"))
      return stockHistoryRepository.getStockHistoryByStockIdAndTimePeriodForYTD(id);

    Integer period = null;

    switch (type) {
      case "ONE_YEAR" -> period = 365;
      case "SIX_MONTHS" -> period = 180;
      case "ONE_MONTH" -> period = 30;
    }

    if (period != null)
      return stockHistoryRepository.getStockHistoryByStockIdAndTimePeriod(id, period);
    else return stockHistoryRepository.getStockHistoryByStockIdAndTimePeriod(id, type);
  }

  //todo margin buy i sell
  public ResponseEntity<?> buyStock(StockRequest stockRequest, User user) {

    Stock stock = getStockBySymbol(stockRequest.getStockSymbol());
    BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));

    if (stockRequest.getStop() == 0 && stockRequest.getLimit() == 0) {
      List<UserStock> userStocksToBuy = findStocksForSale(stockRequest.getStockSymbol(), user.getId(), stockRequest.getAmount());

      if (stockRequest.isAllOrNone()){
//        if (!gotAll) return ResponseEntity.status(500).body("No user has all the needed stocks, buy from the company");

        //todo nadji koji ima sve
      }
      else {

        //prodji i kupi
      }

    }
    else {
      //todo buy with limits
      System.out.println("buy with limits");
    }

    return null;
  }

  private List<UserStock> findStocksForSale(String stockSymbol, long buyingUserId, int amount){
    gotAll = false;
    List<UserStock> userStockToTryBuying = new ArrayList<>();
    int collectedAmount = 0;
    List<UserStock> allUserStocks = userStockService.findAll();

    for (UserStock userStock: allUserStocks) {
      if (userStock.getStock().getSymbol().equals(stockSymbol) && userStock.getAmountForSale() != 0 && userStock.getUser().getId() != buyingUserId){
        userStockToTryBuying.add(userStock);
        collectedAmount += userStock.getAmountForSale();
      }

      if (collectedAmount >= amount) {
        gotAll = true;
        return userStockToTryBuying;
      }
    }

    return userStockToTryBuying;
  }



  public ResponseEntity<?> sellStock(StockRequest stockRequest, User user) {
    if (stockRequest.getStop() == 0 && stockRequest.getLimit() == 0){
      Optional<UserStock> userStock = userStockService.findUserStockByUserIdAndStockSymbol(user.getId(), stockRequest.getStockSymbol());

      //premestamo iz amount u amount_for_sale
      userStock.get().setAmount(userStock.get().getAmount() - stockRequest.getAmount());
      userStock.get().setAmountForSale(stockRequest.getAmount());
      return ResponseEntity.ok().body(userStockService.save(userStock.get()));
    }
    else{
      //todo sell with limits
      System.out.println("sell with limits");
    }

    return null;
  }
}
