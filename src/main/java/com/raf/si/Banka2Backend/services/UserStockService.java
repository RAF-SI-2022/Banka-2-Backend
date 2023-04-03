package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.services.interfaces.UserStockServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStockService implements UserStockServiceInterface {

    @Autowired
    public UserStockService() {
    }
}
