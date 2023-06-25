package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.exceptions.StockNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.Exchange;
import rs.edu.raf.si.bank2.main.models.mariadb.Stock;
import rs.edu.raf.si.bank2.main.models.mariadb.StockHistory;
import rs.edu.raf.si.bank2.main.repositories.mariadb.ExchangeRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.StockHistoryRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.StockRepository;

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
}
