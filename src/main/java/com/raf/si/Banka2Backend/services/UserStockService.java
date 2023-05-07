package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import com.raf.si.Banka2Backend.repositories.mariadb.UserStocksRepository;
import com.raf.si.Banka2Backend.services.interfaces.UserStockServiceInterface;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStockService implements UserStockServiceInterface {

    private final UserStocksRepository userStocksRepository;

    @Autowired
    public UserStockService(UserStocksRepository userStocksRepository) {
        this.userStocksRepository = userStocksRepository;
    }

    @Override
    public Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol) {
        return userStocksRepository.findUserStockByUserIdAndStockSymbol(userId, stockSymbol);
    }

    @Override
    public UserStock save(UserStock userStock) {
        return userStocksRepository.save(userStock);
    }

    @Override
    public List<UserStock> findAll() {
        return userStocksRepository.findAll();
    }

    @Override
    public List<UserStock> findAllForUser(long userId) {
        return userStocksRepository.findUserStocksByUserId(userId);
    }

    @Override
    public UserStock removeFromMarket(long userId, String stockSymbol) {
        Optional<UserStock> userStock = userStocksRepository.findUserStockByUserIdAndStockSymbol(userId, stockSymbol);
        userStock.get().setAmount(userStock.get().getAmount() + userStock.get().getAmountForSale());
        userStock.get().setAmountForSale(0);
        return userStocksRepository.save(userStock.get());
    }
}
