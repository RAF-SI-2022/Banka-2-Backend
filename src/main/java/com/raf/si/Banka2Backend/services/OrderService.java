package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.OrderNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import com.raf.si.Banka2Backend.models.mariadb.orders.OrderStatus;
import com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository;
import com.raf.si.Banka2Backend.services.interfaces.OrderServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements OrderServiceInterface {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
        if(order.isPresent()) {
            order.get().setStatus(status);
            return this.orderRepository.save(order.get());
        }
        throw new OrderNotFoundException(orderId);
    }
}
