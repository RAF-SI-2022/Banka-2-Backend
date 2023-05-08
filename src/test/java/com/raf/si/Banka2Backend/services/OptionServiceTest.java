package com.raf.si.Banka2Backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.raf.si.Banka2Backend.exceptions.OptionNotFoundException;
import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.exceptions.UserNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.repositories.mariadb.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionRepository mockOptionRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private StockService mockStockService;

    @Mock
    private UserOptionRepository mockUserOptionRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private StockRepository mockStockRepository;

    @Mock
    private UserStocksRepository mockUserStocksRepository;

    private OptionService optionServiceUnderTest;

    @BeforeEach
    void setUp() {
        optionServiceUnderTest = new OptionService(
                mockOptionRepository,
                mockUserService,
                mockStockService,
                mockUserOptionRepository,
                mockUserRepository,
                mockStockRepository,
                mockUserStocksRepository);
    }

    @Test
    void testFindAll() {
        // Setup
        final List<Option> expectedResult = List.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());

        // Configure OptionRepository.findAll(...).
        final List<Option> options = List.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findAll()).thenReturn(options);

        // Run the test
        final List<Option> result = optionServiceUnderTest.findAll();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAll_OptionRepositoryReturnsNoItems() {
        // Setup
        when(mockOptionRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Option> result = optionServiceUnderTest.findAll();

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testSave() {
        // Setup
        final Option option = Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build();
        final Option expectedResult = Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build();

        // Configure OptionRepository.save(...).
        final Option option1 = Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build();
        when(mockOptionRepository.save(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build()))
                .thenReturn(option1);

        // Run the test
        final Option result = optionServiceUnderTest.save(option);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindById() {
        // Setup
        final Optional<Option> expectedResult = Optional.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());

        // Configure OptionRepository.findById(...).
        final Optional<Option> option = Optional.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findById(0L)).thenReturn(option);

        // Run the test
        final Optional<Option> result = optionServiceUnderTest.findById(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindById_OptionRepositoryReturnsAbsent() {
        // Setup
        when(mockOptionRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final Optional<Option> result = optionServiceUnderTest.findById(0L);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindByStock() {
        // Setup
        final List<Option> expectedResult = List.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());

        // Configure OptionRepository.findAllByStockSymbol(...).
        final List<Option> options = List.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findAllByStockSymbol("STOCKSYMBOL")).thenReturn(options);

        // Run the test
        final List<Option> result = optionServiceUnderTest.findByStock("stockSymbol");

        // Verify the results
        assertEquals(expectedResult, result);
    }

//    @Test
//    void testFindByStock_OptionRepositoryFindAllByStockSymbolReturnsNoItems() {
//        // Setup
//        when(mockOptionRepository.findAllByStockSymbol("symbol")).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final List<Option> result = optionServiceUnderTest.findByStock("stockSymbol");
//
//        // Verify the results
//        assertEquals(Collections.emptyList(), result);
//        verify(mockOptionRepository)
//                .saveAll(List.of(Option.builder()
//                        .stockSymbol("stockSymbol")
//                        .contractSymbol("contractSymbol")
//                        .optionType("optionType")
//                        .strike(0.0)
//                        .impliedVolatility(0.0)
//                        .price(0.0)
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .openInterest(0)
//                        .contractSize(0)
//                        .maintenanceMargin(0.0)
//                        .bid(0.0)
//                        .ask(0.0)
//                        .changePrice(0.0)
//                        .percentChange(0.0)
//                        .inTheMoney(false)
//                        .build()));
//    }

    @Test
    void testFindByStockAndDate() {
        // Setup
        final List<Option> expectedResult = List.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());

        // Configure OptionRepository.findAllByStockSymbolAndExpirationDate(...).
        final List<Option> options = List.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findAllByStockSymbolAndExpirationDate("STOCKSYMBOL", LocalDate.of(2020, 1, 1)))
                .thenReturn(options);

        // Run the test
        final List<Option> result = optionServiceUnderTest.findByStockAndDate("stockSymbol", "01-01-2020");

        // Verify the results
        assertEquals(expectedResult, result);
    }

//    @Test
//    void testFindByStockAndDate_OptionRepositoryFindAllByStockSymbolAndExpirationDateReturnsNoItems() {
//        // Setup
//        when(mockOptionRepository.findAllByStockSymbolAndExpirationDate("symbol", LocalDate.of(2020, 1, 1)))
//                .thenReturn(Collections.emptyList());
//
//        // Run the test
//        final List<Option> result = optionServiceUnderTest.findByStockAndDate("stockSymbol", "regularDate");
//
//        // Verify the results
//        assertEquals(Collections.emptyList(), result);
//        verify(mockOptionRepository).deleteAll();
//        verify(mockOptionRepository)
//                .saveAll(List.of(Option.builder()
//                        .stockSymbol("stockSymbol")
//                        .contractSymbol("contractSymbol")
//                        .optionType("optionType")
//                        .strike(0.0)
//                        .impliedVolatility(0.0)
//                        .price(0.0)
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .openInterest(0)
//                        .contractSize(0)
//                        .maintenanceMargin(0.0)
//                        .bid(0.0)
//                        .ask(0.0)
//                        .changePrice(0.0)
//                        .percentChange(0.0)
//                        .inTheMoney(false)
//                        .build()));
//    }

    @Test
    void testBuyStockUsingOption() {
        // Setup
        final UserStock expectedResult = UserStock.builder()
                .user(User.builder().build())
                .stock(Stock.builder().build())
                .amount(0)
                .amountForSale(0)
                .build();

        // Configure UserOptionRepository.findById(...).
        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(15.0)
                        .impliedVolatility(0.0)
                        .price(17.0)
                        .expirationDate(LocalDate.of(2060, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(true)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2060, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build());
        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);

        when(mockUserRepository.findById(0L))
                .thenReturn(Optional.of(User.builder().build()));
        when(mockStockRepository.findStockBySymbol("stockSymbol"))
                .thenReturn(Optional.of(Stock.builder().build()));

        // Configure UserStocksRepository.save(...).
        final UserStock userStock = UserStock.builder()
                .user(User.builder().build())
                .stock(Stock.builder().build())
                .amount(0)
                .amountForSale(0)
                .build();
        when(mockUserStocksRepository.save(UserStock.builder()
                        .user(User.builder().build())
                        .stock(Stock.builder().build())
                        .amount(0)
                        .amountForSale(0)
                        .build()))
                .thenReturn(userStock);

        // Run the test
        final UserStock result = optionServiceUnderTest.buyStockUsingOption(0L, 0L);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockUserOptionRepository).deleteById(0L);
    }

    @Test
    void testBuyStockUsingOption_UserOptionRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockUserOptionRepository.findById(0L)).thenReturn(Optional.empty());
        when(mockUserRepository.findById(0L))
                .thenReturn(Optional.of(User.builder().build()));

        // Run the test
        assertThrows(OptionNotFoundException.class, () -> optionServiceUnderTest.buyStockUsingOption(0L, 0L));
    }

    @Test
    void testBuyStockUsingOption_StockRepositoryReturnsAbsent() {
        // Setup
//        final String expectedResult = "Stock with symbol <" + "stockSymbol" + "> not found.";

        // Configure UserOptionRepository.findById(...).
        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(15.0)
                        .impliedVolatility(0.0)
                        .price(17.0)
                        .expirationDate(LocalDate.of(2060, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(true)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2060, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build());
        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);

        when(mockUserRepository.findById(0L)).thenReturn(Optional.of(User.builder().build()));
        when(mockStockRepository.findStockBySymbol("stockSymbol")).thenReturn(Optional.empty());


        assertThrows(StockNotFoundException.class, () -> {
            optionServiceUnderTest.buyStockUsingOption(0L, 0L);
        });
    }

//    @Test
//    void testBuyStockUsingOption_StockRepositoryReturnsAbsent() {
//        // Setup
//        // Configure UserOptionRepository.findById(...).
//        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
//                .user(User.builder().build())
//                .option(Option.builder()
//                        .stockSymbol("stockSymbol")
//                        .contractSymbol("contractSymbol")
//                        .optionType("optionType")
//                        .strike(0.0)
//                        .impliedVolatility(0.0)
//                        .price(0.0)
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .openInterest(0)
//                        .contractSize(0)
//                        .maintenanceMargin(0.0)
//                        .bid(0.0)
//                        .ask(0.0)
//                        .changePrice(0.0)
//                        .percentChange(0.0)
//                        .inTheMoney(false)
//                        .build())
//                .premium(0.0)
//                .amount(0)
//                .type("optionType")
//                .expirationDate(LocalDate.of(2020, 1, 1))
//                .strike(0.0)
//                .stockSymbol("stockSymbol")
//                .build());
//        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);
//
//        when(mockUserRepository.findById(0L))
//                .thenReturn(Optional.of(User.builder().build()));
//        when(mockStockRepository.findStockBySymbol("stockSymbol")).thenReturn(Optional.empty());
//
//        // Run the test
//        assertThrows(StockNotFoundException.class, () -> optionServiceUnderTest.buyStockUsingOption(0L, 0L));
//    }

    @Test
    void testGetUserOptions() {
        // Setup
        final List<UserOption> expectedResult = List.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2020, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build());

        // Configure UserOptionRepository.getUserOptionsByUserId(...).
        final List<UserOption> userOptions = List.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2020, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build());
        when(mockUserOptionRepository.getUserOptionsByUserId(0L)).thenReturn(userOptions);

        // Run the test
        final List<UserOption> result = optionServiceUnderTest.getUserOptions(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetUserOptions_UserOptionRepositoryReturnsNoItems() {
        // Setup
        when(mockUserOptionRepository.getUserOptionsByUserId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<UserOption> result = optionServiceUnderTest.getUserOptions(0L);

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testSellStockUsingOption() {
        optionServiceUnderTest.sellStockUsingOption(0L, 0L);
    }

    @Test
    void testBuyOption() {
        // Setup
        final UserOption expectedResult = UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2020, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build();

        // Configure OptionRepository.findById(...).
        final Optional<Option> option = Optional.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findById(0L)).thenReturn(option);

        when(mockUserService.findById(0L)).thenReturn(Optional.of(User.builder().build()));

        // Configure UserOptionRepository.save(...).
        final UserOption userOption = UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2020, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build();
        when(mockUserOptionRepository.save(UserOption.builder()
                        .user(User.builder().build())
                        .option(Option.builder()
                                .stockSymbol("stockSymbol")
                                .contractSymbol("contractSymbol")
                                .optionType("optionType")
                                .strike(0.0)
                                .impliedVolatility(0.0)
                                .price(0.0)
                                .expirationDate(LocalDate.of(2020, 1, 1))
                                .openInterest(0)
                                .contractSize(0)
                                .maintenanceMargin(0.0)
                                .bid(0.0)
                                .ask(0.0)
                                .changePrice(0.0)
                                .percentChange(0.0)
                                .inTheMoney(false)
                                .build())
                        .premium(0.0)
                        .amount(0)
                        .type("optionType")
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .strike(0.0)
                        .stockSymbol("stockSymbol")
                        .build()))
                .thenReturn(userOption);

        // Run the test
        final UserOption result = optionServiceUnderTest.buyOption(0L, 0L, 0, 0.0);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockOptionRepository)
                .save(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build());
    }

    @Test
    void testBuyOption_OptionRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockOptionRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(OptionNotFoundException.class, () -> optionServiceUnderTest.buyOption(0L, 0L, 0, 0.0));
    }

    @Test
    void testBuyOption_UserServiceReturnsAbsent() {
        // Setup
        // Configure OptionRepository.findById(...).
        final Optional<Option> option = Optional.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findById(0L)).thenReturn(option);

        when(mockUserService.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(UserNotFoundException.class, () -> optionServiceUnderTest.buyOption(0L, 0L, 0, 0.0));
    }

    @Test
    void testBuyOption_UserServiceThrowsUserNotFoundException() {
        // Setup
        // Configure OptionRepository.findById(...).
        final Optional<Option> option = Optional.of(Option.builder()
                .stockSymbol("stockSymbol")
                .contractSymbol("contractSymbol")
                .optionType("optionType")
                .strike(0.0)
                .impliedVolatility(0.0)
                .price(0.0)
                .expirationDate(LocalDate.of(2020, 1, 1))
                .openInterest(0)
                .contractSize(0)
                .maintenanceMargin(0.0)
                .bid(0.0)
                .ask(0.0)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(false)
                .build());
        when(mockOptionRepository.findById(0L)).thenReturn(option);

        when(mockUserService.findById(0L)).thenThrow(UserNotFoundException.class);

        // Run the test
        assertThrows(UserNotFoundException.class, () -> optionServiceUnderTest.buyOption(0L, 0L, 0, 0.0));
    }

    @Test
    void testGetUserOptionsByIdAndStockSymbol() {
        // Setup
        final List<UserOption> expectedResult = List.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2020, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build());

        // Configure UserOptionRepository.getUserOptionsByUserIdAndStockSymbol(...).
        final List<UserOption> userOptions = List.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(0.0)
                        .expirationDate(LocalDate.of(2020, 1, 1))
                        .openInterest(0)
                        .contractSize(0)
                        .maintenanceMargin(0.0)
                        .bid(0.0)
                        .ask(0.0)
                        .changePrice(0.0)
                        .percentChange(0.0)
                        .inTheMoney(false)
                        .build())
                .premium(0.0)
                .amount(0)
                .type("optionType")
                .expirationDate(LocalDate.of(2020, 1, 1))
                .strike(0.0)
                .stockSymbol("stockSymbol")
                .build());
        when(mockUserOptionRepository.getUserOptionsByUserIdAndStockSymbol(0L, "stockSymbol"))
                .thenReturn(userOptions);

        // Run the test
        final List<UserOption> result = optionServiceUnderTest.getUserOptionsByIdAndStockSymbol(0L, "stockSymbol");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetUserOptionsByIdAndStockSymbol_UserOptionRepositoryReturnsNoItems() {
        // Setup
        when(mockUserOptionRepository.getUserOptionsByUserIdAndStockSymbol(0L, "stockSymbol"))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<UserOption> result = optionServiceUnderTest.getUserOptionsByIdAndStockSymbol(0L, "stockSymbol");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

//    @Test
//    void testSellOption() {
//        // Setup
//        final UserOption expectedResult = UserOption.builder()
//                .user(User.builder().build())
//                .option(Option.builder()
//                        .stockSymbol("stockSymbol")
//                        .contractSymbol("contractSymbol")
//                        .optionType("optionType")
//                        .strike(0.0)
//                        .impliedVolatility(0.0)
//                        .price(0.0)
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .openInterest(0)
//                        .contractSize(0)
//                        .maintenanceMargin(0.0)
//                        .bid(0.0)
//                        .ask(0.0)
//                        .changePrice(0.0)
//                        .percentChange(0.0)
//                        .inTheMoney(false)
//                        .build())
//                .premium(0.0)
//                .amount(0)
//                .type("optionType")
//                .expirationDate(LocalDate.of(2020, 1, 1))
//                .strike(0.0)
//                .stockSymbol("stockSymbol")
//                .build();
//
//        // Configure UserOptionRepository.findById(...).
//        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
//                .user(User.builder().build())
//                .option(Option.builder()
//                        .stockSymbol("stockSymbol")
//                        .contractSymbol("contractSymbol")
//                        .optionType("optionType")
//                        .strike(0.0)
//                        .impliedVolatility(0.0)
//                        .price(0.0)
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .openInterest(0)
//                        .contractSize(0)
//                        .maintenanceMargin(0.0)
//                        .bid(0.0)
//                        .ask(0.0)
//                        .changePrice(0.0)
//                        .percentChange(0.0)
//                        .inTheMoney(false)
//                        .build())
//                .premium(0.0)
//                .amount(0)
//                .type("optionType")
//                .expirationDate(LocalDate.of(2020, 1, 1))
//                .strike(0.0)
//                .stockSymbol("stockSymbol")
//                .build());
//        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);
//
//        // Configure UserOptionRepository.save(...).
//        final UserOption userOption1 = UserOption.builder()
//                .user(User.builder().build())
//                .option(Option.builder()
//                        .stockSymbol("stockSymbol")
//                        .contractSymbol("contractSymbol")
//                        .optionType("optionType")
//                        .strike(0.0)
//                        .impliedVolatility(0.0)
//                        .price(0.0)
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .openInterest(0)
//                        .contractSize(0)
//                        .maintenanceMargin(0.0)
//                        .bid(0.0)
//                        .ask(0.0)
//                        .changePrice(0.0)
//                        .percentChange(0.0)
//                        .inTheMoney(false)
//                        .build())
//                .premium(0.0)
//                .amount(0)
//                .type("optionType")
//                .expirationDate(LocalDate.of(2020, 1, 1))
//                .strike(0.0)
//                .stockSymbol("stockSymbol")
//                .build();
//        when(mockUserOptionRepository.save(UserOption.builder()
//                        .user(null)
//                        .option(Option.builder()
//                                .stockSymbol("stockSymbol")
//                                .contractSymbol("contractSymbol")
//                                .optionType("optionType")
//                                .strike(0.0)
//                                .impliedVolatility(0.0)
//                                .price(0.0)
//                                .expirationDate(LocalDate.of(2020, 1, 1))
//                                .openInterest(0)
//                                .contractSize(0)
//                                .maintenanceMargin(0.0)
//                                .bid(0.0)
//                                .ask(0.0)
//                                .changePrice(0.0)
//                                .percentChange(0.0)
//                                .inTheMoney(false)
//                                .build())
//                        .premium(0.0)
//                        .amount(0)
//                        .type("optionType")
//                        .expirationDate(LocalDate.of(2020, 1, 1))
//                        .strike(0.0)
//                        .stockSymbol("stockSymbol")
//                        .build()))
//                .thenReturn(userOption1);
//
//        // Run the test
//        final UserOption result = optionServiceUnderTest.sellOption(0L, 0.0);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }

    @Test
    void testSellOption_UserOptionRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockUserOptionRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(OptionNotFoundException.class, () -> optionServiceUnderTest.sellOption(0L, 0.0));
    }

    @Test
    void testGetFromExternalApi() {
        // Setup
        // https://query1.finance.yahoo.com/v7/finance/options/AAPL?date=1577836800
        Option option1 = Option.builder()
                .id(null)
                .stockSymbol("AAPL")
                .contractSymbol("AAPL230602C00085000")
                .optionType("CALL")
                .strike(85.0)
                .impliedVolatility(1.1035201074218752)
                .price(80.17)
                .expirationDate(LocalDate.parse("2023-06-02"))
                .openInterest(2)
                .contractSize(100)
                .maintenanceMargin(4008.5)
                .bid(87.7)
                .ask(89.65)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(true)
                .build();

        Option option2 = Option.builder()
                .id(null)
                .stockSymbol("AAPL")
                .contractSymbol("AAPL230602C00100000")
                .optionType("CALL")
                .strike(100.0)
                .impliedVolatility(0.9531254687499999)
                .price(73.61)
                .expirationDate(LocalDate.parse("2023-06-02"))
                .openInterest(1)
                .contractSize(100)
                .maintenanceMargin(3680.5)
                .bid(73.05)
                .ask(74.5)
                .changePrice(8.190002)
                .percentChange(12.519111)
                .inTheMoney(true)
                .build();

        Option option3 = Option.builder()
                .id(null)
                .stockSymbol("AAPL")
                .contractSymbol("AAPL230602C00110000")
                .optionType("CALL")
                .strike(110.0)
                .impliedVolatility(0.804689453125)
                .price(63.63)
                .expirationDate(LocalDate.parse("2023-06-02"))
                .openInterest(1)
                .contractSize(100)
                .maintenanceMargin(3181.5)
                .bid(63.45)
                .ask(64.1)
                .changePrice(8.799999)
                .percentChange(16.049606)
                .inTheMoney(true)
                .build();

        Collection<Option> first3FromApi = List.of(option1,option2,option3);

        // Run the test
        final List<Option> result = optionServiceUnderTest.getFromExternalApi("AAPL", "1685664000");


        // Verify the results
        assertEquals(first3FromApi, result.subList(0, 3));
    }
}
