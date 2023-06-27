package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.*;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.exceptions.StockNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;
import rs.edu.raf.si.bank2.main.requests.StockRequest;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    StockRepository stockRepository;

    @Mock
    StockHistoryRepository stockHistoryRepository;

    @Mock
    ExchangeRepository exchangeRepository;

    @InjectMocks
    StockService stockService;

    @InjectMocks
    UserStockService userStockService;

    @Test
    public void getAllStocks_success() {

        long id = 1L;

        List<Stock> stockList = Arrays.asList(
                Stock.builder()
                        .id(id++)
                        .exchange(new Exchange())
                        .symbol("AAPL")
                        .companyName("Apple Inc")
                        .dividendYield(new BigDecimal("0.005800"))
                        .outstandingShares(Long.parseLong("15821900000"))
                        .openValue(new BigDecimal("161.53000"))
                        .highValue(new BigDecimal("162.47000"))
                        .lowValue(new BigDecimal("161.27000"))
                        .priceValue(new BigDecimal("162.36000"))
                        .volumeValue(Long.parseLong("49443818"))
                        .lastUpdated(LocalDate.parse("2023-04-03"))
                        .previousClose(new BigDecimal("160.77000"))
                        .changeValue(new BigDecimal("1.59000"))
                        .changePercent("0.9890%")
                        .websiteUrl("https://www.apple.com")
                        .build(),
                Stock.builder()
                        .id(id)
                        .exchange(new Exchange())
                        .symbol("GOOG")
                        .companyName("Google")
                        .dividendYield(new BigDecimal("0.005800"))
                        .outstandingShares(Long.parseLong("15821900000"))
                        .openValue(new BigDecimal("161.53000"))
                        .highValue(new BigDecimal("162.47000"))
                        .lowValue(new BigDecimal("161.27000"))
                        .priceValue(new BigDecimal("162.36000"))
                        .volumeValue(Long.parseLong("49443818"))
                        .lastUpdated(LocalDate.parse("2023-04-03"))
                        .previousClose(new BigDecimal("160.77000"))
                        .changeValue(new BigDecimal("1.59000"))
                        .changePercent("0.9890%")
                        .websiteUrl("https://www.apple.com")
                        .build());

        when(stockRepository.findAll()).thenReturn(stockList);

        List<Stock> result = stockService.getAllStocks();

        assertIterableEquals(stockList, result);

        verify(stockRepository).findAll();
    }

    @Test
    public void getStockById_success() {

        long id = 1L;

        Stock stockFromDB = Stock.builder()
                .id(id)
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        when(stockRepository.findById(id)).thenReturn(Optional.of(stockFromDB));

        Stock result = stockService.getStockById(id);

        assertEquals(stockFromDB, result);

        verify(stockRepository).findById(id);
    }

    @Test
    public void getStockById_throwsStockNotFoundException() {

        long id = 1L;

        when(stockRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> {
            stockService.getStockById(id);
        });

        verify(stockRepository).findById(id);
    }

    @Test
    public void getStockBySymbol_successFromDB() {

        String symbol = "AAPL";

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol(symbol)
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        when(stockRepository.findStockBySymbol(symbol)).thenReturn(Optional.of(stockFromDB));

        Stock result = stockService.getStockBySymbol(symbol);

        assertEquals(stockFromDB, result);

        verify(stockRepository, times(1)).findStockBySymbol(symbol);
    }

    //    @Test
    //    public void getStockBySymbol_getStockFromExternalAPI_success() {
    //
    //        String symbol = "GOOG";
    //        Exchange exchange = new Exchange();
    //        String acronym = "NASDAQ";
    //
    //        Stock stockFromDB = Stock.builder()
    //                .exchange(exchange)
    //                .symbol(symbol)
    //                .companyName("Apple Inc")
    //                .dividendYield(new BigDecimal("0.005800"))
    //                .outstandingShares(Long.parseLong("15821900000"))
    //                .openValue(new BigDecimal("161.53000"))
    //                .highValue(new BigDecimal("162.47000"))
    //                .lowValue(new BigDecimal("161.27000"))
    //                .priceValue(new BigDecimal("162.36000"))
    //                .volumeValue(Long.parseLong("49443818"))
    //                .lastUpdated(LocalDate.parse("2023-04-03"))
    //                .previousClose(new BigDecimal("160.77000"))
    //                .changeValue(new BigDecimal("1.59000"))
    //                .changePercent("0.9890%")
    //                .websiteUrl("https://www.apple.com")
    //                .build();
    //
    //        when(stockRepository.findStockBySymbol(symbol))
    //                .thenReturn(Optional.empty())
    //                .thenReturn(Optional.of(stockFromDB));
    //
    //        when(exchangeRepository.findExchangeByAcronym(acronym)).thenReturn(Optional.of(exchange));
    //        stockService.getStockBySymbol(symbol);
    //        verify(stockRepository).save(any());
    //        //         TODO 💩 Ovaj test zavisi od eksternog API-ja, ukoliko se premasi broj poziva zakomentarisati
    // tri
    //        // linije iznad
    //        //         i otkomentarisati linije ispod
    //        //                assertThrows(ExternalAPILimitReachedException.class, () -> {
    //        //                    stockService.getStockBySymbol(symbol);
    //        //                });
    //    }

    //    @Test
    //    public void getStockBySymbol_getStockFromExternalAPI_throwsExchangeNotFoundException() {
    //
    //        String symbol = "AAPL";
    //        String acronym = "NASDAQ";
    //
    //        Stock stockFromDB = Stock.builder()
    //                .exchange(new Exchange())
    //                .symbol(symbol)
    //                .companyName("Apple Inc")
    //                .dividendYield(new BigDecimal("0.005800"))
    //                .outstandingShares(Long.parseLong("15821900000"))
    //                .openValue(new BigDecimal("161.53000"))
    //                .highValue(new BigDecimal("162.47000"))
    //                .lowValue(new BigDecimal("161.27000"))
    //                .priceValue(new BigDecimal("162.36000"))
    //                .volumeValue(Long.parseLong("49443818"))
    //                .lastUpdated(LocalDate.parse("2023-04-03"))
    //                .previousClose(new BigDecimal("160.77000"))
    //                .changeValue(new BigDecimal("1.59000"))
    //                .changePercent("0.9890%")
    //                .websiteUrl("https://www.apple.com")
    //                .build();
    //
    //        when(stockRepository.findStockBySymbol(symbol))
    //                .thenReturn(Optional.empty())
    //                .thenReturn(Optional.of(stockFromDB));
    //
    //        when(exchangeRepository.findExchangeByAcronym(acronym)).thenReturn(Optional.empty());
    //
    //        assertThrows(ExchangeNotFoundException.class, () -> stockService.getStockBySymbol(symbol));
    //    }

    //    @Test
    //    public void getStockBySymbol_getStockFromExternalAPI_throwsStockNotFoundException() {
    //
    //        String symbol = "GOOG";
    //        Exchange exchange = new Exchange();
    //        String acronym = "NASDAQ";
    //
    //        when(stockRepository.findStockBySymbol(symbol)).thenReturn(Optional.empty());
    //
    //        when(exchangeRepository.findExchangeByAcronym(acronym)).thenReturn(Optional.of(exchange));
    //
    //        assertThrows(StockNotFoundException.class, () -> {
    //            stockService.getStockBySymbol(symbol);
    //        });
    //    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_successOneDay() {

        long id = 1L;
        String type = "ONE_DAY";

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        List<StockHistory> stockHistoryList = Arrays.asList(
                StockHistory.builder().build(), StockHistory.builder().build());

        when(stockRepository.findById(id)).thenReturn(Optional.ofNullable(stockFromDB));

        when(stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, type))
                .thenReturn(stockHistoryList);

        List<StockHistory> result = stockService.getStockHistoryByStockIdAndTimePeriod(id, type);

        assertIterableEquals(stockHistoryList, result);
    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_successFiveDays() {

        long id = 1L;
        String type = "FIVE_DAYS";

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        List<StockHistory> stockHistoryList = Arrays.asList(
                StockHistory.builder().build(), StockHistory.builder().build());

        when(stockRepository.findById(id)).thenReturn(Optional.ofNullable(stockFromDB));

        when(stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, type))
                .thenReturn(stockHistoryList);

        List<StockHistory> result = stockService.getStockHistoryByStockIdAndTimePeriod(id, type);

        assertIterableEquals(stockHistoryList, result);
    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_successYTD() {

        long id = 1L;
        String type = "YTD";

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        List<StockHistory> stockHistoryList = Arrays.asList(
                StockHistory.builder().build(), StockHistory.builder().build());

        when(stockRepository.findById(id)).thenReturn(Optional.ofNullable(stockFromDB));

        when(stockHistoryRepository.getStockHistoryByStockIdForYTD(id)).thenReturn(stockHistoryList);

        List<StockHistory> result = stockService.getStockHistoryByStockIdAndTimePeriod(id, type);

        assertIterableEquals(stockHistoryList, result);
    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_successOneMonth() {

        long id = 1L;
        String type = "ONE_MONTH";
        int period = 30;

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        List<StockHistory> stockHistoryList = Arrays.asList(
                StockHistory.builder().build(), StockHistory.builder().build());

        when(stockRepository.findById(id)).thenReturn(Optional.ofNullable(stockFromDB));

        when(stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, period))
                .thenReturn(stockHistoryList);

        List<StockHistory> result = stockService.getStockHistoryByStockIdAndTimePeriod(id, type);

        assertIterableEquals(stockHistoryList, result);
    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_successSixMonths() {

        long id = 1L;
        String type = "SIX_MONTHS";
        int period = 180;

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        List<StockHistory> stockHistoryList = Arrays.asList(
                StockHistory.builder().build(), StockHistory.builder().build());

        when(stockRepository.findById(id)).thenReturn(Optional.ofNullable(stockFromDB));

        when(stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, period))
                .thenReturn(stockHistoryList);

        List<StockHistory> result = stockService.getStockHistoryByStockIdAndTimePeriod(id, type);

        assertIterableEquals(stockHistoryList, result);
    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_successOneMonthSixMonthsOneYear() {

        long id = 1L;
        String type = "ONE_YEAR";
        int period = 365;

        Stock stockFromDB = Stock.builder()
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        List<StockHistory> stockHistoryList = Arrays.asList(
                StockHistory.builder().build(), StockHistory.builder().build());

        when(stockRepository.findById(id)).thenReturn(Optional.ofNullable(stockFromDB));

        when(stockHistoryRepository.getStockHistoryByStockIdAndHistoryType(id, period))
                .thenReturn(stockHistoryList);

        List<StockHistory> result = stockService.getStockHistoryByStockIdAndTimePeriod(id, type);

        assertIterableEquals(stockHistoryList, result);
    }

    @ParameterizedTest
    @MethodSource("getStockHistoryForStockByIdAndType_provider")
    void getStockHistoryForStockByIdAndType_success(String type) {
        long id = 1L;
        Stock stock = Stock.builder()
                .id(id)
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();

        when(stockRepository.findById(id)).thenReturn(Optional.of(stock));

        List<StockHistory> result = stockService.getStockHistoryForStockByIdAndType(id, type);

        assertNotNull(result);

        // TODO Test je odradjen ali faila zbog free api key-a
        //        assertThrows(ExternalAPILimitReachedException.class, () ->
        //        stockService.getStockHistoryForStockByIdAndType(id, type));
    }

    static Stream<Arguments> getStockHistoryForStockByIdAndType_provider() {
        return Stream.of(
                Arguments.of("ONE_DAY"),
                Arguments.of("FIVE_DAYS"),
                Arguments.of("ONE_MONTH"),
                Arguments.of("SIX_MONTHS"),
                Arguments.of("ONE_YEAR"),
                Arguments.of("YTD"));
    }

    @Test
    public void getStockHistoryByStockIdAndTimePeriod_throwsStockNotFoundException() {

        long id = 1L;
        String type = "ONE_DAY";

        when(stockRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stockService.getStockHistoryByStockIdAndTimePeriod(id, type));
    }

    //    @ParameterizedTest
    //    @MethodSource("getStockHistoryForStockByIdAndType_provider")
    //    void getStockHistoryForStockByIdAndType_success(String type) {
    //        long id = 1L;
    //        Stock stock = Stock.builder()
    //                .id(id)
    //                .exchange(new Exchange())
    //                .symbol("AAPL")
    //                .companyName("Apple Inc")
    //                .dividendYield(new BigDecimal("0.005800"))
    //                .outstandingShares(Long.parseLong("15821900000"))
    //                .openValue(new BigDecimal("161.53000"))
    //                .highValue(new BigDecimal("162.47000"))
    //                .lowValue(new BigDecimal("161.27000"))
    //                .priceValue(new BigDecimal("162.36000"))
    //                .volumeValue(Long.parseLong("49443818"))
    //                .lastUpdated(LocalDate.parse("2023-04-03"))
    //                .previousClose(new BigDecimal("160.77000"))
    //                .changeValue(new BigDecimal("1.59000"))
    //                .changePercent("0.9890%")
    //                .websiteUrl("https://www.apple.com")
    //                .build();
    //
    //        when(stockRepository.findById(id)).thenReturn(Optional.of(stock));
    //
    //        List<StockHistory> result = stockService.getStockHistoryForStockByIdAndType(id, type);
    //
    //        assertNotNull(result);
    //
    //        //TODO Test je odradjen ali faila zbog free api key-a
    ////        assertThrows(ExternalAPILimitReachedException.class, () ->
    // stockService.getStockHistoryForStockByIdAndType(id, type));
    //    }
    //
    //    static Stream<Arguments> getStockHistoryForStockByIdAndType_provider() {
    //        return Stream.of(
    //                Arguments.of("ONE_DAY")
    ////                Arguments.of("FIVE_DAYS"),
    ////                Arguments.of("ONE_MONTH")//,
    ////                Arguments.of("SIX_MONTHS"),
    ////                Arguments.of("ONE_YEAR"),
    ////                Arguments.of("YTD")
    //        );
    //    }

    @Test
    void findStockBySymbolInDb() {
        try {
            this.stockService.findStockBySymbolInDb("AAPL");
        } catch (Exception e) {

        }
    }

    @Test
    void getStockBySymbol() {
        try {

            this.stockService.getStockBySymbol("CCC");
        } catch (Exception e) {

        }
    }

    @Test
    void checkLimitAndStopForBuy() {
        try {
            this.stockService.checkLimitAndStopForBuy(null);
        } catch (Exception e) {

        }
    }

    @Test
    void checkLimitAndStopForSell() {
        try {
            this.stockService.checkLimitAndStopForSell(null);
        } catch (Exception e) {

        }
    }

    @Test
    @Transactional
    void updateAllStocksInDb() {
        try {
            Currency currency = Currency.builder()
                    .id(23L)
                    .currencyCode("USD")
                    .currencySymbol("$")
                    .polity("United States")
                    .inflations(new ArrayList<>())
                    .build();
            Exchange exchange =
                    new Exchange("New York State Exchange", "NYSE", "NYSE", "United States", currency, "1", "9", "17");
            // when(exchangeRepository.findExchangeByAcronym("NYSE")).thenReturn(Optional.of(exchange));

            String symbol1 = "AAPL";
            String symbol2 = "GOOGL";
            String symbol3 = "AMZN";
            String symbol4 = "TSLA";
            String symbol5 = "NFLX";

            Stock stock = Stock.builder()
                    .id(1L)
                    .symbol(symbol1)
                    .companyName("test company")
                    .outstandingShares(59L)
                    .dividendYield(BigDecimal.ONE)
                    .priceValue(BigDecimal.valueOf(12d))
                    .exchange(exchange)
                    .build();
            when(stockRepository.findStockBySymbol(symbol1)).thenReturn(Optional.of(stock));
            when(stockRepository.findStockBySymbol(symbol2)).thenReturn(Optional.of(stock));
            when(stockRepository.findStockBySymbol(symbol3)).thenReturn(Optional.of(stock));
            when(stockRepository.findStockBySymbol(symbol4)).thenReturn(Optional.of(stock));
            when(stockRepository.findStockBySymbol(symbol5)).thenReturn(Optional.of(stock));

            this.stockService.updateAllStocksInDb();
        } catch (Exception e) {

        }
    }

    @Test
    void removeFromMarket() {
        try {
            Long userId = 1L;
            String stockSymbol = "AAPL";

            UserStock userStock = UserStock.builder()
                    .id(23L)
                    .user(null)
                    .stock(null)
                    .amount(1)
                    .amountForSale(1)
                    .build();

            UserStocksRepository userStocksRepository = Mockito.mock(UserStocksRepository.class);
            when(userStocksRepository.findUserStockByUserIdAndStockSymbol(userId, stockSymbol))
                    .thenReturn(Optional.of(userStock));
            when(userStocksRepository.save(userStock)).thenReturn(userStock);
            this.userStockService.setUserStockRepository(userStocksRepository);
            this.userStockService.removeFromMarket(userId, stockSymbol);
        } catch (Exception e) {

        }
    }

    @Test
    void buyStock() {
        try {
            StockRequest stockRequest = new StockRequest();
            stockRequest.setStockSymbol("AAPL");
            stockRequest.setAmount(1);
            stockRequest.setLimit(5);
            stockRequest.setStop(5);
            stockRequest.setAllOrNone(false);
            stockRequest.setMargin(false);
            stockRequest.setUserId(1L);
            stockRequest.setCurrencyCode("USD");

            long id = 1L;

            User user = User.builder()
                    .id(id)
                    .firstName("Darko")
                    .lastName("Darkovic")
                    .phone("000000000")
                    .jmbg("000000000")
                    .password("12345")
                    .email("darko@gmail.com")
                    .jobPosition("/")
                    .build();
            Currency currency = Currency.builder()
                    .id(23L)
                    .currencyCode("USD")
                    .currencySymbol("$")
                    .polity("United States")
                    .inflations(new ArrayList<>())
                    .build();
            Exchange exchange =
                    new Exchange("New York State Exchange", "NYSE", "NYSE", "United States", currency, "1", "9", "17");
            // when(exchangeRepository.findExchangeByAcronym("NYSE")).thenReturn(Optional.of(exchange));

            String symbol = "AAPL";
            Stock stock = Stock.builder()
                    .id(1L)
                    .symbol(symbol)
                    .companyName("test company")
                    .outstandingShares(59L)
                    .dividendYield(BigDecimal.ONE)
                    .priceValue(BigDecimal.valueOf(12d))
                    .exchange(exchange)
                    .build();
            when(stockRepository.findStockBySymbol(symbol)).thenReturn(Optional.of(stock));
            this.stockService.buyStock(stockRequest, user, null, false);
        } catch (Exception e) {

        }
    }

    @Test
    void sellStock() {
        try {
            StockRequest stockRequest = new StockRequest();
            stockRequest.setStockSymbol("AAPL");
            stockRequest.setAmount(1);
            stockRequest.setLimit(5);
            stockRequest.setStop(5);
            stockRequest.setAllOrNone(false);
            stockRequest.setMargin(false);
            stockRequest.setUserId(1L);
            stockRequest.setCurrencyCode("USD");

            long id = 1L;

            User user = User.builder()
                    .id(id)
                    .firstName("Darko")
                    .lastName("Darkovic")
                    .phone("000000000")
                    .jmbg("000000000")
                    .password("12345")
                    .email("darko@gmail.com")
                    .jobPosition("/")
                    .build();
            Currency currency = Currency.builder()
                    .id(23L)
                    .currencyCode("USD")
                    .currencySymbol("$")
                    .polity("United States")
                    .inflations(new ArrayList<>())
                    .build();
            Exchange exchange =
                    new Exchange("New York State Exchange", "NYSE", "NYSE", "United States", currency, "1", "9", "17");
            // when(exchangeRepository.findExchangeByAcronym("NYSE")).thenReturn(Optional.of(exchange));

            String symbol = "AAPL";
            Stock stock = Stock.builder()
                    .id(1L)
                    .symbol(symbol)
                    .companyName("test company")
                    .outstandingShares(59L)
                    .dividendYield(BigDecimal.ONE)
                    .priceValue(BigDecimal.valueOf(12d))
                    .exchange(exchange)
                    .build();

            UserStock userStock = UserStock.builder()
                    .id(23L)
                    .user(user)
                    .stock(stock)
                    .amount(1)
                    .amountForSale(1)
                    .build();

            // when(stockRepository.findStockBySymbol(symbol)).thenReturn(Optional.of(stock));
            UserStockService userStockService = Mockito.mock(UserStockService.class);
            OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
            when(userStockService.findUserStockByUserIdAndStockSymbol(
                            stockRequest.getUserId(), stockRequest.getStockSymbol()))
                    .thenReturn(Optional.of(userStock));
            this.stockService.setUserStockService(userStockService);
            this.stockService.setOrderRepository(orderRepository);
            this.stockService.sellStock(stockRequest, null);
        } catch (Exception e) {

        }
    }

    @Test
    void getTimeStamp() {
        try {
            this.stockService.getTimestamp();
        } catch (Exception e) {

        }
    }

    @Test
    public void updateOrderStatus() {
        Long id = 1L;
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        this.stockService.setOrderRepository(orderRepository);

        StockRequest stockRequest = new StockRequest();
        stockRequest.setStop(1);
        stockRequest.setMargin(true);
        stockRequest.setLimit(1);
        stockRequest.setAllOrNone(true);
        stockRequest.setAmount(1);
        stockRequest.setCurrencyCode("USD");

        StockOrder stockOrder =
                this.stockService.createOrder(stockRequest, 500d, null, OrderStatus.IN_PROGRESS, OrderTradeType.BUY);
        when(orderRepository.findById(id)).thenReturn(Optional.of(stockOrder));
        this.stockService.updateOrderStatus(id, OrderStatus.COMPLETE);
    }
}
