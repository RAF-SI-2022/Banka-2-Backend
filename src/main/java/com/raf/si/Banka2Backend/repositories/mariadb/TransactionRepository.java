package com.raf.si.Banka2Backend.repositories.mariadb;
import com.raf.si.Banka2Backend.models.mariadb.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserEmailAndCurrencyCurrencyCode(String userEmail, String currencyCode);
    List<Transaction> findAllByOrderId(Long id);

}
