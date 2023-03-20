package com.raf.si.Banka2Backend.repositories.exchange;

import com.raf.si.Banka2Backend.models.exchange.Forex;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForexRepository extends MongoRepository<Forex, String> {}
