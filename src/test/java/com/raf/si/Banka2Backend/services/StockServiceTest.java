package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.ExchangeNotFoundException;
import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.models.mariadb.orders.StockOrder;
import com.raf.si.Banka2Backend.repositories.mariadb.ExchangeRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.StockHistoryRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.StockRepository;
import com.raf.si.Banka2Backend.requests.StockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository mockStockRepository;
    @Mock
    private StockHistoryRepository mockStockHistoryRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private UserStockService mockUserStockService;
    @Mock
    private CurrencyService mockCurrencyService;
    @Mock
    private TransactionService mockTransactionService;
    @Mock
    private ExchangeRepository mockExchangeRepository;
    @Mock
    private BalanceService mockBalanceService;
    @Mock
    private OrderRepository mockOrderRepository;

    private StockService stockServiceUnderTest;

    @BeforeEach
    void setUp() {
        stockServiceUnderTest = new StockService(mockStockRepository, mockStockHistoryRepository, mockUserService,
                mockUserStockService, mockCurrencyService, mockTransactionService, mockExchangeRepository,
                mockBalanceService, mockOrderRepository);
    }

    @Test
    void testGetAllStocks() {
        // Setup
        final List<Stock> expectedResult = List.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());

        // Configure StockRepository.findAll(...).
        final List<Stock> stocks = List.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findAll()).thenReturn(stocks);

        // Run the test
        final List<Stock> result = stockServiceUnderTest.getAllStocks();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllStocks_StockRepositoryReturnsNoItems() {
        // Setup
        when(mockStockRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Stock> result = stockServiceUnderTest.getAllStocks();

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetStockById() {
        // Setup
        final Stock expectedResult = Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build();

        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        // Run the test
        final Stock result = stockServiceUnderTest.getStockById(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetStockById_StockRepositoryReturnsAbsent() {
        // Setup
        when(mockStockRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(StockNotFoundException.class, () -> stockServiceUnderTest.getStockById(0L));
    }

    @Test
    void testGetStockBySymbol() {
        // Setup
        final Stock expectedResult = Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build();

        // Configure StockRepository.findStockBySymbol(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findStockBySymbol("symbol")).thenReturn(stock);

        // Run the test
        final Stock result = stockServiceUnderTest.getStockBySymbol("symbol");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetStockBySymbol_StockRepositoryFindStockBySymbolReturnsAbsent() {
        // Setup
        final Stock expectedResult = Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build();
        when(mockStockRepository.findStockBySymbol("symbol")).thenReturn(Optional.empty());

        // Configure ExchangeRepository.findExchangeByAcronym(...).
        final Exchange exchange1 = new Exchange();
        exchange1.setId(0L);
        exchange1.setExchangeName("exchangeName");
        exchange1.setAcronym("acronym");
        exchange1.setMicCode("micCode");
        exchange1.setPolity("polity");
        final Optional<Exchange> exchange = Optional.of(exchange1);
        when(mockExchangeRepository.findExchangeByAcronym("acronym")).thenReturn(exchange);

        // Run the test
        final Stock result = stockServiceUnderTest.getStockBySymbol("symbol");

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockStockRepository).save(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
    }

    @Test
    void testGetStockBySymbol_ExchangeRepositoryReturnsAbsent() {
        // Setup
        when(mockStockRepository.findStockBySymbol("symbol")).thenReturn(Optional.empty());
        when(mockExchangeRepository.findExchangeByAcronym("acronym")).thenReturn(Optional.empty());

        // Run the test
        assertThrows(ExchangeNotFoundException.class, () -> stockServiceUnderTest.getStockBySymbol("symbol"));
    }

    @Test
    void testGetStockHistoryByStockIdAndTimePeriod() {
        // Setup
        final List<StockHistory> expectedResult = List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build());

        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        // Configure StockHistoryRepository.getStockHistoryByStockIdForYTD(...).
        final List<StockHistory> stockHistories = List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build());
        when(mockStockHistoryRepository.getStockHistoryByStockIdForYTD(0L)).thenReturn(stockHistories);

        // Configure StockHistoryRepository.getStockHistoryByStockIdAndHistoryType(...).
        final List<StockHistory> stockHistories1 = List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build());
        when(mockStockHistoryRepository.getStockHistoryByStockIdAndHistoryType(0L, 0)).thenReturn(stockHistories1);

        // Configure StockHistoryRepository.getStockHistoryByStockIdAndHistoryType(...).
        final List<StockHistory> stockHistories2 = List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build());
        when(mockStockHistoryRepository.getStockHistoryByStockIdAndHistoryType(0L, "type")).thenReturn(stockHistories2);

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryByStockIdAndTimePeriod(0L, "type");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetStockHistoryByStockIdAndTimePeriod_StockRepositoryReturnsAbsent() {
        // Setup
        when(mockStockRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(StockNotFoundException.class,
                () -> stockServiceUnderTest.getStockHistoryByStockIdAndTimePeriod(0L, "type"));
    }

    @Test
    void testGetStockHistoryByStockIdAndTimePeriod_StockHistoryRepositoryGetStockHistoryByStockIdForYTDReturnsNoItems() {
        // Setup
        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        when(mockStockHistoryRepository.getStockHistoryByStockIdForYTD(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryByStockIdAndTimePeriod(0L, "type");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetStockHistoryByStockIdAndTimePeriod_StockHistoryRepositoryGetStockHistoryByStockIdAndHistoryType1ReturnsNoItems() {
        // Setup
        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        when(mockStockHistoryRepository.getStockHistoryByStockIdAndHistoryType(0L, 0))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryByStockIdAndTimePeriod(0L, "type");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetStockHistoryByStockIdAndTimePeriod_StockHistoryRepositoryGetStockHistoryByStockIdAndHistoryType2ReturnsNoItems() {
        // Setup
        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        when(mockStockHistoryRepository.getStockHistoryByStockIdAndHistoryType(0L, "type"))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryByStockIdAndTimePeriod(0L, "type");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetStockHistoryForStockByIdAndType() {
        // Setup
        final List<StockHistory> expectedResult = List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build());

        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryForStockByIdAndType(0L, "type");

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockStockHistoryRepository).deleteByStockIdAndType(0L, StockHistoryType.ONE_DAY);
        verify(mockStockHistoryRepository).saveAll(List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build()));
    }

    @Test
    void testGetStockHistoryForStockByIdAndType_StockRepositoryReturnsAbsent() {
        // Setup
        when(mockStockRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(StockNotFoundException.class,
                () -> stockServiceUnderTest.getStockHistoryForStockByIdAndType(0L, "type"));
    }

    @Test
    void testGetStockHistoryForStockByIdAndType_StockHistoryRepositoryGetStockHistoryByStockIdForYTDReturnsNoItems() {
        // Setup
        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        when(mockStockHistoryRepository.getStockHistoryByStockIdForYTD(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryForStockByIdAndType(0L, "type");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetStockHistoryForStockByIdAndType_StockHistoryRepositoryGetStockHistoryByStockIdAndHistoryType1ReturnsNoItems() {
        // Setup
        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        when(mockStockHistoryRepository.getStockHistoryByStockIdAndHistoryType(0L, 0))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryForStockByIdAndType(0L, "type");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetStockHistoryForStockByIdAndType_StockHistoryRepositoryGetStockHistoryByStockIdAndHistoryType2ReturnsNoItems() {
        // Setup
        // Configure StockRepository.findById(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findById(0L)).thenReturn(stock);

        when(mockStockHistoryRepository.getStockHistoryByStockIdAndHistoryType(0L, "type"))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.getStockHistoryForStockByIdAndType(0L, "type");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testParseResponse() {
        // Setup
        final HttpResponse<String> mockResponse = mock(HttpResponse.class);
        final Stock stock = Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build();
        final List<StockHistory> expectedResult = List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build());

        // Run the test
        final List<StockHistory> result = stockServiceUnderTest.parseResponse(mockResponse, stock, "type");

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockStockHistoryRepository).deleteByStockIdAndType(0L, StockHistoryType.ONE_DAY);
        verify(mockStockHistoryRepository).saveAll(List.of(StockHistory.builder()
                .openValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .closeValue(new BigDecimal("0.00"))
                .volumeValue(0L)
                .onDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .type(StockHistoryType.ONE_DAY)
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .build()));
    }

    @Test
    void testBuyStock() {
        // Setup
        final StockRequest stockRequest = new StockRequest();
        stockRequest.setStockSymbol("symbol");
        stockRequest.setAmount(0);
        stockRequest.setLimit(0);
        stockRequest.setStop(0);
        stockRequest.setAllOrNone(false);
        stockRequest.setMargin(false);
        stockRequest.setUserId(0L);
        stockRequest.setCurrencyCode("currencyCode");

        final User user = User.builder()
                .id(0L)
                .email("email")
                .dailyLimit(0.0)
                .build();
        final StockOrder stockOrder = new StockOrder();
        stockOrder.setStockLimit(0);
        stockOrder.setStop(0);
        stockOrder.setAllOrNone(false);
        stockOrder.setMargin(false);
        stockOrder.setCurrencyCode("currencyCode");

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure StockRepository.findStockBySymbol(...).
        final Optional<Stock> stock = Optional.of(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());
        when(mockStockRepository.findStockBySymbol("symbol")).thenReturn(stock);

        when(mockBalanceService.findBalanceByUserIdAndCurrency(0L, "currencyCode")).thenReturn(Balance.builder()
                .amount(0.0f)
                .build());

        // Run the test
        final ResponseEntity<?> result = stockServiceUnderTest.buyStock(stockRequest, user, stockOrder);

        // Verify the results
        assertEquals(expectedResult, result);

        // Confirm OrderRepository.save(...).
        final StockOrder entity = new StockOrder();
        entity.setStockLimit(0);
        entity.setStop(0);
        entity.setAllOrNone(false);
        entity.setMargin(false);
        entity.setCurrencyCode("currencyCode");
        verify(mockOrderRepository).save(entity);
        verify(mockUserService).save(User.builder()
                .id(0L)
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).reserveAmount(0.0f, "email", "currencyCode");
    }

    @Test
    void testBuyStock_StockRepositoryFindStockBySymbolReturnsAbsent() {
        // Setup
        final StockRequest stockRequest = new StockRequest();
        stockRequest.setStockSymbol("symbol");
        stockRequest.setAmount(0);
        stockRequest.setLimit(0);
        stockRequest.setStop(0);
        stockRequest.setAllOrNone(false);
        stockRequest.setMargin(false);
        stockRequest.setUserId(0L);
        stockRequest.setCurrencyCode("currencyCode");

        final User user = User.builder()
                .id(0L)
                .email("email")
                .dailyLimit(0.0)
                .build();
        final StockOrder stockOrder = new StockOrder();
        stockOrder.setStockLimit(0);
        stockOrder.setStop(0);
        stockOrder.setAllOrNone(false);
        stockOrder.setMargin(false);
        stockOrder.setCurrencyCode("currencyCode");

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);
        when(mockStockRepository.findStockBySymbol("symbol")).thenReturn(Optional.empty());

        // Configure ExchangeRepository.findExchangeByAcronym(...).
        final Exchange exchange1 = new Exchange();
        exchange1.setId(0L);
        exchange1.setExchangeName("exchangeName");
        exchange1.setAcronym("acronym");
        exchange1.setMicCode("micCode");
        exchange1.setPolity("polity");
        final Optional<Exchange> exchange = Optional.of(exchange1);
        when(mockExchangeRepository.findExchangeByAcronym("acronym")).thenReturn(exchange);

        when(mockBalanceService.findBalanceByUserIdAndCurrency(0L, "currencyCode")).thenReturn(Balance.builder()
                .amount(0.0f)
                .build());

        // Run the test
        final ResponseEntity<?> result = stockServiceUnderTest.buyStock(stockRequest, user, stockOrder);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockStockRepository).save(Stock.builder()
                .id(0L)
                .symbol("symbol")
                .companyName("companyName")
                .outstandingShares(0L)
                .dividendYield(new BigDecimal("0.00"))
                .priceValue(new BigDecimal("0.00"))
                .openValue(new BigDecimal("0.00"))
                .lowValue(new BigDecimal("0.00"))
                .highValue(new BigDecimal("0.00"))
                .changeValue(new BigDecimal("0.00"))
                .previousClose(new BigDecimal("0.00"))
                .volumeValue(0L)
                .lastUpdated(LocalDate.of(2020, 1, 1))
                .changePercent("changePercent")
                .exchange(new Exchange())
                .websiteUrl("websiteUrl")
                .build());

        // Confirm OrderRepository.save(...).
        final StockOrder entity = new StockOrder();
        entity.setStockLimit(0);
        entity.setStop(0);
        entity.setAllOrNone(false);
        entity.setMargin(false);
        entity.setCurrencyCode("currencyCode");
        verify(mockOrderRepository).save(entity);
        verify(mockUserService).save(User.builder()
                .id(0L)
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).reserveAmount(0.0f, "email", "currencyCode");
    }

    @Test
    void testBuyStock_ExchangeRepositoryReturnsAbsent() {
        // Setup
        final StockRequest stockRequest = new StockRequest();
        stockRequest.setStockSymbol("symbol");
        stockRequest.setAmount(0);
        stockRequest.setLimit(0);
        stockRequest.setStop(0);
        stockRequest.setAllOrNone(false);
        stockRequest.setMargin(false);
        stockRequest.setUserId(0L);
        stockRequest.setCurrencyCode("currencyCode");

        final User user = User.builder()
                .id(0L)
                .email("email")
                .dailyLimit(0.0)
                .build();
        final StockOrder stockOrder = new StockOrder();
        stockOrder.setStockLimit(0);
        stockOrder.setStop(0);
        stockOrder.setAllOrNone(false);
        stockOrder.setMargin(false);
        stockOrder.setCurrencyCode("currencyCode");

        when(mockStockRepository.findStockBySymbol("symbol")).thenReturn(Optional.empty());
        when(mockExchangeRepository.findExchangeByAcronym("acronym")).thenReturn(Optional.empty());

        // Run the test
        assertThrows(ExchangeNotFoundException.class,
                () -> stockServiceUnderTest.buyStock(stockRequest, user, stockOrder));
    }

    @Test
    void testSellStock() {
        // Setup
        final StockRequest stockRequest = new StockRequest();
        stockRequest.setStockSymbol("symbol");
        stockRequest.setAmount(0);
        stockRequest.setLimit(0);
        stockRequest.setStop(0);
        stockRequest.setAllOrNone(false);
        stockRequest.setMargin(false);
        stockRequest.setUserId(0L);
        stockRequest.setCurrencyCode("currencyCode");

        final StockOrder stockOrder = new StockOrder();
        stockOrder.setStockLimit(0);
        stockOrder.setStop(0);
        stockOrder.setAllOrNone(false);
        stockOrder.setMargin(false);
        stockOrder.setCurrencyCode("currencyCode");

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure UserStockService.findUserStockByUserIdAndStockSymbol(...).
        final Optional<UserStock> userStock = Optional.of(UserStock.builder()
                .user(User.builder()
                        .id(0L)
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .amount(0)
                .build());
        when(mockUserStockService.findUserStockByUserIdAndStockSymbol(0L, "symbol")).thenReturn(userStock);

        // Configure OrderRepository.save(...).
        final StockOrder stockOrder1 = new StockOrder();
        stockOrder1.setStockLimit(0);
        stockOrder1.setStop(0);
        stockOrder1.setAllOrNone(false);
        stockOrder1.setMargin(false);
        stockOrder1.setCurrencyCode("currencyCode");
        final StockOrder entity = new StockOrder();
        entity.setStockLimit(0);
        entity.setStop(0);
        entity.setAllOrNone(false);
        entity.setMargin(false);
        entity.setCurrencyCode("currencyCode");
        when(mockOrderRepository.save(entity)).thenReturn(stockOrder1);

        // Run the test
        final ResponseEntity<?> result = stockServiceUnderTest.sellStock(stockRequest, stockOrder);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testSellStock_UserStockServiceReturnsAbsent() {
        // Setup
        final StockRequest stockRequest = new StockRequest();
        stockRequest.setStockSymbol("symbol");
        stockRequest.setAmount(0);
        stockRequest.setLimit(0);
        stockRequest.setStop(0);
        stockRequest.setAllOrNone(false);
        stockRequest.setMargin(false);
        stockRequest.setUserId(0L);
        stockRequest.setCurrencyCode("currencyCode");

        final StockOrder stockOrder = new StockOrder();
        stockOrder.setStockLimit(0);
        stockOrder.setStop(0);
        stockOrder.setAllOrNone(false);
        stockOrder.setMargin(false);
        stockOrder.setCurrencyCode("currencyCode");

        when(mockUserStockService.findUserStockByUserIdAndStockSymbol(0L, "symbol")).thenReturn(Optional.empty());

        // Run the test
        assertThrows(NoSuchElementException.class, () -> stockServiceUnderTest.sellStock(stockRequest, stockOrder));
    }

    @Test
    void testGetAllUserStocks() {
        // Setup
        final List<UserStock> expectedResult = List.of(UserStock.builder()
                .user(User.builder()
                        .id(0L)
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .amount(0)
                .build());

        // Configure UserStockService.findAllForUser(...).
        final List<UserStock> userStocks = List.of(UserStock.builder()
                .user(User.builder()
                        .id(0L)
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .stock(Stock.builder()
                        .id(0L)
                        .symbol("symbol")
                        .companyName("companyName")
                        .outstandingShares(0L)
                        .dividendYield(new BigDecimal("0.00"))
                        .priceValue(new BigDecimal("0.00"))
                        .openValue(new BigDecimal("0.00"))
                        .lowValue(new BigDecimal("0.00"))
                        .highValue(new BigDecimal("0.00"))
                        .changeValue(new BigDecimal("0.00"))
                        .previousClose(new BigDecimal("0.00"))
                        .volumeValue(0L)
                        .lastUpdated(LocalDate.of(2020, 1, 1))
                        .changePercent("changePercent")
                        .exchange(new Exchange())
                        .websiteUrl("websiteUrl")
                        .build())
                .amount(0)
                .build());
        when(mockUserStockService.findAllForUser(0L)).thenReturn(userStocks);

        // Run the test
        final List<UserStock> result = stockServiceUnderTest.getAllUserStocks(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllUserStocks_UserStockServiceReturnsNoItems() {
        // Setup
        when(mockUserStockService.findAllForUser(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<UserStock> result = stockServiceUnderTest.getAllUserStocks(0L);

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }
}
