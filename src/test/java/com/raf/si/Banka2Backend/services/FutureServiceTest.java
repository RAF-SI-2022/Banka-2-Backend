package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.UserNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FutureServiceTest {

    @Mock
    private UserService mockUserService;
    @Mock
    private FutureRepository mockFutureRepository;
    @Mock
    private BalanceService mockBalanceService;

    private FutureService futureServiceUnderTest;

    @BeforeEach
    void setUp() {
        futureServiceUnderTest = new FutureService(mockUserService, mockFutureRepository, mockBalanceService);
    }

    @Test
    void testFindAll() {
        // Setup
        final List<Future> expectedResult = List.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());

        // Configure FutureRepository.findAll(...).
        final List<Future> futures = List.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findAll()).thenReturn(futures);

        // Run the test
        final List<Future> result = futureServiceUnderTest.findAll();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAll_FutureRepositoryReturnsNoItems() {
        // Setup
        when(mockFutureRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Future> result = futureServiceUnderTest.findAll();

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testFindById() {
        // Setup
        final Optional<Future> expectedResult = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());

        // Configure FutureRepository.findFutureById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findFutureById(0L)).thenReturn(future);

        // Run the test
        final Optional<Future> result = futureServiceUnderTest.findById(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindById_FutureRepositoryReturnsAbsent() {
        // Setup
        when(mockFutureRepository.findFutureById(0L)).thenReturn(Optional.empty());

        // Run the test
        final Optional<Future> result = futureServiceUnderTest.findById(0L);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindFuturesByFutureName() {
        // Setup
        final Optional<List<Future>> expectedResult = Optional.of(List.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build()));

        // Configure FutureRepository.findFuturesByFutureName(...).
        final Optional<List<Future>> futures = Optional.of(List.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build()));
        when(mockFutureRepository.findFuturesByFutureName("futureName")).thenReturn(futures);

        // Run the test
        final Optional<List<Future>> result = futureServiceUnderTest.findFuturesByFutureName("futureName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindFuturesByFutureName_FutureRepositoryReturnsAbsent() {
        // Setup
        when(mockFutureRepository.findFuturesByFutureName("futureName")).thenReturn(Optional.empty());

        // Run the test
        final Optional<List<Future>> result = futureServiceUnderTest.findFuturesByFutureName("futureName");

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindFuturesByFutureName_FutureRepositoryReturnsNoItems() {
        // Setup
        when(mockFutureRepository.findFuturesByFutureName("futureName"))
                .thenReturn(Optional.of(Collections.emptyList()));

        // Run the test
        final Optional<List<Future>> result = futureServiceUnderTest.findFuturesByFutureName("futureName");

        // Verify the results
        assertEquals(Optional.of(Collections.emptyList()), result);
    }

    @Test
    void testBuyFuture() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        // Configure UserService.findByEmail(...).
        final Optional<User> user = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findByEmail("fromUserEmail")).thenReturn(user);

        // Configure UserService.findById(...).
        final Optional<User> user1 = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findById(0L)).thenReturn(user1);

        // Configure FutureRepository.findFutureById(...).
        final Optional<Future> future1 = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findFutureById(0L)).thenReturn(future1);

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.buyFuture(futureRequest, "fromUserEmail", 0.0f);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockUserService).save(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).exchangeMoney("fromUserEmail", "email", 0.0f, "USD");
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testBuyFuture_FutureRepositoryFindByIdReturnsAbsent() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);
        when(mockFutureRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.buyFuture(futureRequest, "fromUserEmail", 0.0f);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testBuyFuture_UserServiceFindByEmailReturnsAbsent() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        when(mockUserService.findByEmail("fromUserEmail")).thenReturn(Optional.empty());

        // Configure UserService.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findById(0L)).thenReturn(user);

        // Configure FutureRepository.findFutureById(...).
        final Optional<Future> future1 = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findFutureById(0L)).thenReturn(future1);

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.buyFuture(futureRequest, "fromUserEmail", 0.0f);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockUserService).save(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).exchangeMoney("fromUserEmail", "email", 0.0f, "USD");
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testBuyFuture_UserServiceFindByIdReturnsAbsent() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        // Configure UserService.findByEmail(...).
        final Optional<User> user = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findByEmail("fromUserEmail")).thenReturn(user);

        when(mockUserService.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(NoSuchElementException.class,
                () -> futureServiceUnderTest.buyFuture(futureRequest, "fromUserEmail", 0.0f));
        verify(mockUserService).save(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).exchangeMoney("fromUserEmail", "email", 0.0f, "USD");
    }

    @Test
    void testBuyFuture_UserServiceFindByIdThrowsUserNotFoundException() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        // Configure UserService.findByEmail(...).
        final Optional<User> user = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findByEmail("fromUserEmail")).thenReturn(user);

        when(mockUserService.findById(0L)).thenThrow(UserNotFoundException.class);

        // Run the test
        assertThrows(UserNotFoundException.class,
                () -> futureServiceUnderTest.buyFuture(futureRequest, "fromUserEmail", 0.0f));
        verify(mockUserService).save(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).exchangeMoney("fromUserEmail", "email", 0.0f, "USD");
    }

    @Test
    void testBuyFuture_FutureRepositoryFindFutureByIdReturnsAbsent() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        // Configure UserService.findByEmail(...).
        final Optional<User> user = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findByEmail("fromUserEmail")).thenReturn(user);

        // Configure UserService.findById(...).
        final Optional<User> user1 = Optional.of(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        when(mockUserService.findById(0L)).thenReturn(user1);

        when(mockFutureRepository.findFutureById(0L)).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.buyFuture(futureRequest, "fromUserEmail", 0.0f);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockUserService).save(User.builder()
                .email("email")
                .dailyLimit(0.0)
                .build());
        verify(mockBalanceService).exchangeMoney("fromUserEmail", "email", 0.0f, "USD");
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testUpdateFuture() {
        // Setup
        final Future future = Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build();

        // Run the test
        futureServiceUnderTest.updateFuture(future);

        // Verify the results
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testSellFuture() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        // Configure FutureRepository.findFutureById(...).
        final Optional<Future> future1 = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findFutureById(0L)).thenReturn(future1);

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.sellFuture(futureRequest);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testSellFuture_FutureRepositoryFindByIdReturnsAbsent() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);
        when(mockFutureRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.sellFuture(futureRequest);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testSellFuture_FutureRepositoryFindFutureByIdReturnsAbsent() {
        // Setup
        final FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setId(0L);
        futureRequest.setUserId(0L);
        futureRequest.setFutureName("futureName");
        futureRequest.setPrice(0);
        futureRequest.setLimit(0);
        futureRequest.setStop(0);

        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure FutureRepository.findById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findById(0L)).thenReturn(future);

        when(mockFutureRepository.findFutureById(0L)).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.sellFuture(futureRequest);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testRemoveFromMarket() {
        // Setup
        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Configure FutureRepository.findFutureById(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findFutureById(0L)).thenReturn(future);

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.removeFromMarket(0L);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockFutureRepository).save(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
    }

    @Test
    void testRemoveFromMarket_FutureRepositoryFindFutureByIdReturnsAbsent() {
        // Setup
        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);
        when(mockFutureRepository.findFutureById(0L)).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.removeFromMarket(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testRemoveWaitingSellFuture() {
        // Setup
        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.removeWaitingSellFuture(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testRemoveWaitingBuyFuture() {
        // Setup
        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);

        // Run the test
        final ResponseEntity<?> result = futureServiceUnderTest.removeWaitingBuyFuture(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetWaitingFuturesForUser() {
        // Setup
        // Run the test
        final List<Long> result = futureServiceUnderTest.getWaitingFuturesForUser(0L, "type", "futureName");

        // Verify the results
        assertEquals(List.of(0L), result);
    }

    @Test
    void testFindByName() {
        // Setup
        final Optional<Future> expectedResult = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());

        // Configure FutureRepository.findFutureByFutureName(...).
        final Optional<Future> future = Optional.of(Future.builder()
                .maintenanceMargin(0)
                .forSale(false)
                .user(User.builder()
                        .email("email")
                        .dailyLimit(0.0)
                        .build())
                .build());
        when(mockFutureRepository.findFutureByFutureName("futureName")).thenReturn(future);

        // Run the test
        final Optional<Future> result = futureServiceUnderTest.findByName("futureName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindByName_FutureRepositoryReturnsAbsent() {
        // Setup
        when(mockFutureRepository.findFutureByFutureName("futureName")).thenReturn(Optional.empty());

        // Run the test
        final Optional<Future> result = futureServiceUnderTest.findByName("futureName");

        // Verify the results
        assertEquals(Optional.empty(), result);
    }
}
