package rs.edu.raf.si.bank2.main.repositories.mariadb;

import rs.edu.raf.si.bank2.main.models.mariadb.orders.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long id);
}
