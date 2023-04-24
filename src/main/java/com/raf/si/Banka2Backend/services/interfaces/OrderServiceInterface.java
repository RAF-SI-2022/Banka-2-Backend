package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.orders.Order;

import java.util.List;
import java.util.Optional;

public interface OrderServiceInterface {

    List<Order> findAll();

    Optional<Order> findById();

    List<Order> findByType();
}
