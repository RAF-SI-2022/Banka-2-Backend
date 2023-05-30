package rs.edu.raf.si.bank2.otc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rs.edu.raf.si.bank2.otc.exceptions.OrderNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.otc.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.otc.models.mariadb.orders.OrderTradeType;
import rs.edu.raf.si.bank2.otc.models.mariadb.orders.OrderType;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository mockOrderRepository;

    @Mock
    private StockService mockStockService;

    @Mock
    private BalanceService mockBalanceService;

    private OrderService orderServiceUnderTest;

    @BeforeEach
    void setUp() {
        orderServiceUnderTest = new OrderService(mockOrderRepository, mockStockService, mockBalanceService);
    }

    @Test
    void testFindAll() {
        // Setup
        final List<Order> expectedResult = List.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());

        // Configure OrderRepository.findAll(...).
        final List<Order> orders = List.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());
        when(mockOrderRepository.findAll()).thenReturn(orders);

        // Run the test
        final List<Order> result = orderServiceUnderTest.findAll();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAll_OrderRepositoryReturnsNoItems() {
        // Setup
        when(mockOrderRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Order> result = orderServiceUnderTest.findAll();

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testFindById() {
        // Setup
        final Optional<Order> expectedResult = Optional.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());

        // Configure OrderRepository.findById(...).
        final Optional<Order> order = Optional.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());
        when(mockOrderRepository.findById(0L)).thenReturn(order);

        // Run the test
        final Optional<Order> result = orderServiceUnderTest.findById(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindById_OrderRepositoryReturnsAbsent() {
        // Setup
        when(mockOrderRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final Optional<Order> result = orderServiceUnderTest.findById(0L);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindByType() {
        assertNull(orderServiceUnderTest.findByType());
    }

    @Test
    void testSave() {
        // Setup
        final Order order = Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build();
        final Order expectedResult = Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build();

        // Configure OrderRepository.save(...).
        final Order order1 = Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build();
        when(mockOrderRepository.save(Order.builder()
                        .orderType(OrderType.STOCK)
                        .tradeType(OrderTradeType.BUY)
                        .status(OrderStatus.WAITING)
                        .symbol("stockSymbol")
                        .amount(0)
                        .price(0.0)
                        .user(User.builder().id(0L).email("email").build())
                        .build()))
                .thenReturn(order1);

        // Run the test
        final Order result = orderServiceUnderTest.save(order);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testUpdateOrderStatus() {
        // Setup
        final Order expectedResult = Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build();

        // Configure OrderRepository.findById(...).
        final Optional<Order> order = Optional.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());
        when(mockOrderRepository.findById(0L)).thenReturn(order);

        // Configure OrderRepository.save(...).
        final Order order1 = Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build();
        when(mockOrderRepository.save(Order.builder()
                        .orderType(OrderType.STOCK)
                        .tradeType(OrderTradeType.BUY)
                        .status(OrderStatus.WAITING)
                        .symbol("stockSymbol")
                        .amount(0)
                        .price(0.0)
                        .user(User.builder().id(0L).email("email").build())
                        .build()))
                .thenReturn(order1);

        // Run the test
        final Order result = orderServiceUnderTest.updateOrderStatus(0L, OrderStatus.WAITING);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testUpdateOrderStatus_OrderRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockOrderRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThrows(
                OrderNotFoundException.class, () -> orderServiceUnderTest.updateOrderStatus(0L, OrderStatus.WAITING));
    }

    @Test
    void testFindAllByUserId() {
        // Setup
        final List<Order> expectedResult = List.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());

        // Configure OrderRepository.findAllByUserId(...).
        final List<Order> orders = List.of(Order.builder()
                .orderType(OrderType.STOCK)
                .tradeType(OrderTradeType.BUY)
                .status(OrderStatus.WAITING)
                .symbol("stockSymbol")
                .amount(0)
                .price(0.0)
                .user(User.builder().id(0L).email("email").build())
                .build());
        when(mockOrderRepository.findAllByUserId(0L)).thenReturn(orders);

        // Run the test
        final List<Order> result = orderServiceUnderTest.findAllByUserId(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAllByUserId_OrderRepositoryReturnsNoItems() {
        // Setup
        when(mockOrderRepository.findAllByUserId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Order> result = orderServiceUnderTest.findAllByUserId(0L);

        // Verify the results
        assertEquals(Collections.emptyList(), result);
    }

    //    @Test
    //    void testStartOrder() {
    //        // Setup
    //        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.OK);
    //
    //        // Configure OrderRepository.findById(...).
    //        final Optional<Order> order = Optional.of(Order.builder()
    //                .orderType(OrderType.STOCK)
    //                .tradeType(OrderTradeType.BUY)
    //                .status(OrderStatus.WAITING)
    //                .symbol("stockSymbol")
    //                .amount(0)
    //                .price(0.0)
    //                .user(User.builder().id(0L).email("email").build())
    //                .build());
    //        when(mockOrderRepository.findById(0L)).thenReturn(order);
    //
    //        // Configure StockService.buyStock(...).
    //        final StockRequest stockRequest = new StockRequest();
    //        stockRequest.setStockSymbol("stockSymbol");
    //        stockRequest.setAmount(0);
    //        stockRequest.setLimit(0);
    //        stockRequest.setStop(0);
    //        stockRequest.setAllOrNone(false);
    //        stockRequest.setMargin(false);
    //        stockRequest.setUserId(0L);
    //        final StockOrder stockOrder = new StockOrder();
    //        stockOrder.setOrderType(OrderType.STOCK);
    //        stockOrder.setTradeType(OrderTradeType.BUY);
    //        stockOrder.setStatus(OrderStatus.WAITING);
    //        stockOrder.setSymbol("stockSymbol");
    //        stockOrder.setAmount(0);
    //        stockOrder.setPrice(0.0);
    //        stockOrder.setUser(User.builder().id(0L).email("email").build());
    //        stockOrder.setStockLimit(0);
    //        stockOrder.setStop(0);
    //        stockOrder.setAllOrNone(false);
    //        stockOrder.setMargin(false);
    //        doReturn(new ResponseEntity<>(null, HttpStatus.OK))
    //                .when(mockStockService)
    //                .buyStock(stockRequest, User.builder().id(0L).email("email").build(), stockOrder);
    //
    //        // Configure StockService.sellStock(...).
    //        final StockRequest stockRequest1 = new StockRequest();
    //        stockRequest1.setStockSymbol("stockSymbol");
    //        stockRequest1.setAmount(0);
    //        stockRequest1.setLimit(0);
    //        stockRequest1.setStop(0);
    //        stockRequest1.setAllOrNone(false);
    //        stockRequest1.setMargin(false);
    //        stockRequest1.setUserId(0L);
    //        final StockOrder stockOrder1 = new StockOrder();
    //        stockOrder1.setOrderType(OrderType.STOCK);
    //        stockOrder1.setTradeType(OrderTradeType.BUY);
    //        stockOrder1.setStatus(OrderStatus.WAITING);
    //        stockOrder1.setSymbol("stockSymbol");
    //        stockOrder1.setAmount(0);
    //        stockOrder1.setPrice(0.0);
    //        stockOrder1.setUser(User.builder().id(0L).email("email").build());
    //        stockOrder1.setStockLimit(0);
    //        stockOrder1.setStop(0);
    //        stockOrder1.setAllOrNone(false);
    //        stockOrder1.setMargin(false);
    //        doReturn(new ResponseEntity<>(null, HttpStatus.OK))
    //                .when(mockStockService)
    //                .sellStock(stockRequest1, stockOrder1);
    //
    //        // Configure BalanceService.buyOrSellCurrency(...).
    //        final ForexOrder forexOrder = new ForexOrder();
    //        forexOrder.setOrderType(OrderType.STOCK);
    //        forexOrder.setTradeType(OrderTradeType.BUY);
    //        forexOrder.setStatus(OrderStatus.WAITING);
    //        forexOrder.setSymbol("stockSymbol");
    //        forexOrder.setAmount(0);
    //        forexOrder.setPrice(0.0);
    //        forexOrder.setUser(User.builder().id(0L).email("email").build());
    //        when(mockBalanceService.buyOrSellCurrency("email", "fromCurrencyCode", "toCurrencyCode", 0.0f, 0,
    // forexOrder))
    //                .thenReturn(false);
    //
    //        // Run the test
    //        final ResponseEntity<?> result = orderServiceUnderTest.startOrder(0L);
    //
    //        // Verify the results
    //        assertEquals(expectedResult, result);
    //    }

    @Test
    void testStartOrder_OrderRepositoryReturnsAbsent() {
        // Setup
        final ResponseEntity<?> expectedResult = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockOrderRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<?> result = orderServiceUnderTest.startOrder(0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
