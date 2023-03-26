package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {}
