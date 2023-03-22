package com.raf.si.Banka2Backend.repositories.mongodb;

import com.raf.si.Banka2Backend.models.mongodb.Forex;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForexRepository extends MongoRepository<Forex, String> {}
