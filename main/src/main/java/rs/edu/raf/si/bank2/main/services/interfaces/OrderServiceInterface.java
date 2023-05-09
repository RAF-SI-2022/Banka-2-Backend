package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderStatus;
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
