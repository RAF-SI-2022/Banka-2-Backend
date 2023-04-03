package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.models.mariadb.StockHistory;
import com.raf.si.Banka2Backend.repositories.mariadb.StockHistoryRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.StockRepository;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class StockService {

  private static final String KEY = "J3WDDK9HZ1G71YIP";
  private StockRepository stockRepository;
  private StockHistoryRepository stockHistoryRepository;

  public StockService(
      StockRepository stockRepository, StockHistoryRepository stockHistoryRepository) {
    this.stockRepository = stockRepository;
    this.stockHistoryRepository = stockHistoryRepository;
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
}
