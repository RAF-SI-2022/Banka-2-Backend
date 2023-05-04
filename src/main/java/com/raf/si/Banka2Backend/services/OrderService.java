package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.OrderNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.orders.*;
import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.interfaces.OrderServiceInterface;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements OrderServiceInterface {
    private final com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository orderRepository;
    private final StockService stockService;
    private final BalanceService balanceService;

    @Autowired
    public OrderService(
            com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository orderRepository,
            StockService stockService,
            BalanceService balanceService) {
        this.orderRepository = orderRepository;
        this.stockService = stockService;
        this.balanceService = balanceService;
    }

    @Override
    public List<Order> findAll() {
        return this.orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return this.orderRepository.findById(id);
    }

    @Override
    public List<Order> findByType() {
        return null;
    }

    @Override
    public Order save(Order order) {
        return this.orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> order = this.findById(orderId);
        if (order.isPresent()) {
            order.get().setStatus(status);
            return this.orderRepository.save(order.get());
        }
        throw new OrderNotFoundException(orderId);
    }

    @Override
    public List<Order> findAllByUserId(Long id) {
        return this.orderRepository.findAllByUserId(id);
    }

    @Override
    public ResponseEntity<?> startOrder(Long id) {
        Optional<Order> o = this.orderRepository.findById(id);
        if (o.isPresent()) {
            switch (o.get().getOrderType()) {
                case STOCK:
                    if (o.get().getTradeType() == OrderTradeType.BUY) {
                        return this.stockService.buyStock(
                                this.orderToStockRequest((StockOrder) o.get()),
                                o.get().getUser(),
                                (StockOrder) o.get());
                    } else {
                        return this.stockService.sellStock(
                                this.orderToStockRequest((StockOrder) o.get()), (StockOrder) o.get());
                    }
                case FOREX:
                    boolean result = this.balanceService.buyOrSellCurrency(
                            o.get().getUser().getEmail(),
                            o.get().getSymbol().split(" ")[0],
                            o.get().getSymbol().split(" ")[1],
                            (float) o.get().getPrice(),
                            o.get().getAmount(),
                            (ForexOrder) o.get());
                    if (result) return ResponseEntity.status(HttpStatus.OK).body("Order executed");
                    else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kornisnik nema dovoljno novca");
                case FUTURE:
                    System.out.println("Under construction");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                case OPTION:
                    System.out.println("Under Construction");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    System.out.println("Error");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private StockRequest orderToStockRequest(StockOrder stockOrder) {
        StockRequest stockRequest = new StockRequest();
        stockRequest.setStockSymbol(stockOrder.getSymbol());
        stockRequest.setAmount(stockOrder.getAmount());
        stockRequest.setLimit(stockOrder.getStockLimit());
        stockRequest.setStop(stockOrder.getStop());
        stockRequest.setAllOrNone(stockOrder.isAllOrNone());
        stockRequest.setMargin(stockOrder.isMargin());
        stockRequest.setUserId(stockOrder.getUser().getId());
        return stockRequest;
    }

    //    String stockSymbol;
    //    Integer amount;
    //    Integer limit;
    //    Integer stop;
    //    boolean allOrNone;
    //    boolean margin;
    //    Long userId; //null sa fronta, stavi se u servisima
    //    String currencyCode;
}
