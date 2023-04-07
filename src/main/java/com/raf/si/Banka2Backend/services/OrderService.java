package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Order;
import com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository;
import com.raf.si.Banka2Backend.services.interfaces.OrderServiceInterface;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements OrderServiceInterface {
  private final OrderRepository orderRepository;

  @Autowired
  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public List<Order> findAll() {
    return null;
  }

  @Override
  public Optional<Order> findById() {
    return Optional.empty();
  }

  @Override
  public List<Order> findByType() {
    return null;
  }
}
