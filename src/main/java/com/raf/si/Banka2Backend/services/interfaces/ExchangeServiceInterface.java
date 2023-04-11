package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Exchange;

import java.util.List;
import java.util.Optional;

public interface ExchangeServiceInterface {

    List<Exchange> findAll();

    Optional<Exchange> findById(Long id);

    Optional<Exchange> findByMicCode(String micCode);

    Optional<Exchange> findByAcronym(String acronym);

    boolean isExchangeActive(String micCode);
}
