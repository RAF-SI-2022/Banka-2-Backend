package rs.edu.raf.si.bank2.securities.services.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import rs.edu.raf.si.bank2.securities.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.securities.models.mariadb.orders.OrderStatus;

public interface OrderServiceInterface {

    List<Order> findAll();

    Optional<Order> findById(Long orderId);

    List<Order> findByType();

    Order save(Order order);

    Order updateOrderStatus(Long orderId, OrderStatus status);

    List<Order> findAllByUserId(Long id);

    ResponseEntity<?> startOrder(Long id);
}
