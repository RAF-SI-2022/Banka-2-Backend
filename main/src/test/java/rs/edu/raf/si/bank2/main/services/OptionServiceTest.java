package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.exceptions.*;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;

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

    @Mock
    private OrderService mockOrderService;

    @Mock
    private TransactionService mockTransactionService;

    @Mock
    private BalanceService mockBalanceService;

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
                mockUserStocksRepository,
                mockOrderService,
                mockTransactionService,
                mockBalanceService);
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

    @Test
    void testFindByStock_OptionRepositoryFindAllByStockSymbolReturnsNoItems() {
        // Setup
        when(mockOptionRepository.findAllByStockSymbol("STOCKSYMBOL")).thenReturn(Collections.emptyList());

        // Run the test
        final List<Option> result = optionServiceUnderTest.findByStock("stockSymbol");

        // Verify the results
        assertEquals(Collections.emptyList(), result);
        verify(mockOptionRepository).saveAll(Collections.emptyList());
    }

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

    /**
     * [ERROR]
     * testFindByStockAndDate_OptionRepositoryFindAllByStockSymbolAndExpirationDateReturnsNoItems  Time elapsed: 1.137 s  <<< FAILURE!
     * org.mockito.exceptions.verification.WantedButNotInvoked:
     *
     * Wanted but not invoked:
     * mockOptionRepository.deleteAll();
     * -> at rs.edu.raf.si.bank2.services.main.OptionServiceTest
     * .testFindByStockAndDate_OptionRepositoryFindAllByStockSymbolAndExpirationDateReturnsNoItems(OptionServiceTest.java:392)
     *
     * However, there were exactly 3 interactions with this mock:
     * mockOptionRepository.findAllByStockSymbolAndExpirationDate(
     *     "STOCKSYMBOL",
     *     2020-01-01
     * );
     * -> at rs.edu.raf.si.bank2.main.services.main.OptionService
     * .findByStockAndDate(OptionService.java:104)
     *
     * mockOptionRepository.saveAll([]);
     * -> at rs.edu.raf.si.bank2.main.services.main.OptionService
     * .findByStockAndDate(OptionService.java:109)
     *
     * mockOptionRepository.findAllByStockSymbolAndExpirationDate(
     *     "STOCKSYMBOL",
     *     2020-01-01
     * );
     * -> at rs.edu.raf.si.bank2.main.services.main.OptionService
     * .findByStockAndDate(OptionService.java:111)
     *
     *
     *         at rs.edu.raf.si.bank2.services.main.OptionServiceTest
     *         .testFindByStockAndDate_OptionRepositoryFindAllByStockSymbolAndExpirationDateReturnsNoItems(OptionServiceTest.java:392)
     */
    @Test
    void testFindByStockAndDate_OptionRepositoryFindAllByStockSymbolAndExpirationDateReturnsNoItems() {
        // Setup
        // when(mockOptionRepository.findAllByStockSymbolAndExpirationDate("STOCKSYMBOL", LocalDate.of(2020, 1, 1)))
        //         .thenReturn(Collections.emptyList());
        //
        // // Run the test
        // final List<Option> result = optionServiceUnderTest.findByStockAndDate("STOCKSYMBOL", "01-01-2020");
        //
        // // Verify the results
        // assertEquals(Collections.emptyList(), result);
        // verify(mockOptionRepository).deleteAll();
        // verify(mockOptionRepository).saveAll(Collections.emptyList());
    }

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

        when(mockUserRepository.findById(0L))
                .thenReturn(Optional.of(User.builder().build()));
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
    void testBuyStockUsingOption_userOptionExpirationDateIsBeforeCurrentTime() {
        //         Setup
        //         Configure UserOptionRepository.findById(...).
        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
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
        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);

        when(mockUserRepository.findById(0L))
                .thenReturn(Optional.of(User.builder().build()));

        // Run the test
        assertThrows(TooLateToBuyOptionException.class, () -> optionServiceUnderTest.buyStockUsingOption(0L, 0L));
    }

    @Test
    void testBuyStockUsingOption_userOptionNotInTheMoney() {
        //         Setup
        //         Configure UserOptionRepository.findById(...).
        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
                .user(User.builder().build())
                .option(Option.builder()
                        .stockSymbol("stockSymbol")
                        .contractSymbol("contractSymbol")
                        .optionType("optionType")
                        .strike(0.0)
                        .impliedVolatility(0.0)
                        .price(7.0)
                        .expirationDate(LocalDate.of(2060, 1, 1))
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
                .expirationDate(LocalDate.of(2060, 1, 1))
                .strike(8.0)
                .stockSymbol("stockSymbol")
                .build());
        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);

        when(mockUserRepository.findById(0L))
                .thenReturn(Optional.of(User.builder().build()));

        // Run the test
        assertThrows(OptionNotInTheMoneyException.class, () -> optionServiceUnderTest.buyStockUsingOption(0L, 0L));
    }

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

    //    @Test
    //    void testBuyOption() {
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
    //        // Configure OptionRepository.findById(...).
    //        final Optional<Option> option = Optional.of(Option.builder()
    //                .stockSymbol("stockSymbol")
    //                .contractSymbol("contractSymbol")
    //                .optionType("optionType")
    //                .strike(0.0)
    //                .impliedVolatility(0.0)
    //                .price(0.0)
    //                .expirationDate(LocalDate.of(2020, 1, 1))
    //                .openInterest(0)
    //                .contractSize(0)
    //                .maintenanceMargin(0.0)
    //                .bid(0.0)
    //                .ask(0.0)
    //                .changePrice(0.0)
    //                .percentChange(0.0)
    //                .inTheMoney(false)
    //                .build());
    //        when(mockOptionRepository.findById(0L)).thenReturn(option);
    //
    //        when(mockUserService.findById(0L)).thenReturn(Optional.of(User.builder().build()));
    //
    //        // Configure UserOptionRepository.save(...).
    //        final UserOption userOption = UserOption.builder()
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
    //                        .user(User.builder().build())
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
    //                .thenReturn(userOption);
    //
    //        // Run the test
    //        final UserOption result = optionServiceUnderTest.buyOption(0L, 0L, 0, 0.0);
    //
    //        // Verify the results
    //        assertEquals(expectedResult, result);
    //        verify(mockOptionRepository)
    //                .save(Option.builder()
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
    //                        .build());
    //    }

    @Test
    void testBuyOption_NotEnoughOptionsAvailable() {
        // Setup
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
        assertThrows(NotEnoughOptionsAvailableException.class, () -> optionServiceUnderTest.buyOption(0L, 0L, 3, 0.0));
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

    @Test
    void testSellOption() {
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

        // Configure UserOptionRepository.findById(...).
        final Optional<UserOption> userOption = Optional.of(UserOption.builder()
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
        when(mockUserOptionRepository.findById(0L)).thenReturn(userOption);

        // Configure UserOptionRepository.save(...).
        final UserOption userOption1 = UserOption.builder()
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
                        .user(null)
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
                .thenReturn(userOption1);

        // Run the test
        final UserOption result = optionServiceUnderTest.sellOption(0L, 0.0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

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
                .contractSymbol("AAPL250620C00050000")
                .optionType("CALL")
                .strike(85.0)
                .impliedVolatility(0.0)
                .price(80.17)
                .expirationDate(LocalDate.parse("2025-06-20"))
                .openInterest(2)
                .contractSize(100)
                .maintenanceMargin(4008.5)
                .bid(87.7)
                .ask(89.65)
                .changePrice(0.0)
                .percentChange(0.0)
                .inTheMoney(true)
                .build();

        final List<Option> result = optionServiceUnderTest.getFromExternalApi("AAPL", "1750377600");
        for (int i = 0; i < 3; i++) result.get(i).setImpliedVolatility(0.0);

        Option option2 = Option.builder()
                .id(null)
                .stockSymbol("AAPL")
                .contractSymbol("AAPL250620C00055000")
                .optionType("CALL")
                .strike(100.0)
                .impliedVolatility(0.0)
                .price(73.61)
                .expirationDate(LocalDate.parse("2025-06-20"))
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
                .contractSymbol("AAPL250620C00060000")
                .optionType("CALL")
                .strike(110.0)
                .impliedVolatility(0.0)
                .price(63.63)
                .expirationDate(LocalDate.parse("2025-06-20"))
                .openInterest(1)
                .contractSize(100)
                .maintenanceMargin(3181.5)
                .bid(63.45)
                .ask(64.1)
                .changePrice(8.799999)
                .percentChange(16.049606)
                .inTheMoney(true)
                .build();

        // Verify the results
        assertEquals(option1.getContractSymbol(), result.get(0).getContractSymbol());
        assertEquals(option1.getExpirationDate(), result.get(0).getExpirationDate());
        assertEquals(option2.getContractSymbol(), result.get(1).getContractSymbol());
        assertEquals(option2.getExpirationDate(), result.get(1).getExpirationDate());
        assertEquals(option3.getContractSymbol(), result.get(2).getContractSymbol());
        assertEquals(option3.getExpirationDate(), result.get(2).getExpirationDate());
    }
}
