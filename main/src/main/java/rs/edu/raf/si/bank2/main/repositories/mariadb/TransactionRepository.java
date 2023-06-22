package rs.edu.raf.si.bank2.main.repositories.mariadb;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.si.bank2.main.models.mariadb.Transaction;
import rs.edu.raf.si.bank2.main.models.mariadb.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserEmailAndCurrencyCurrencyCode(String userEmail, String currencyCode);

    List<Transaction> findAllByOrderId(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.order.id = :orderId")
    void updateTransactionStatusesByOrderId(@Param("orderId") Long orderId, @Param("status") TransactionStatus status);
}
