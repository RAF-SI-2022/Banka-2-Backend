package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long id);
}
