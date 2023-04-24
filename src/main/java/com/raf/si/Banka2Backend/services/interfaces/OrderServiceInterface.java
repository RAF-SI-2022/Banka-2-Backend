package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import com.raf.si.Banka2Backend.models.mariadb.orders.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

public interface OrderServiceInterface {

    List<Order> findAll();

    Optional<Order> findById(Long orderId);

    List<Order> findByType();

    Order save(Order order);

    Order updateOrderStatus(Long orderId, OrderStatus status);

    List<Order> findAllByUserId(Long id);

    ResponseEntity<?> startOrder(Long id);
}
