package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Option;
import com.raf.si.Banka2Backend.repositories.mariadb.OptionRepository;
import com.raf.si.Banka2Backend.services.interfaces.OptionServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OptionService implements OptionServiceInterface {

    private final OptionRepository optionRepository;
    private final UserService userService;
    private final StockService stockService;

    @Autowired
    public OptionService(OptionRepository optionRepository, UserService userService, StockService stockService) {
        this.optionRepository = optionRepository;
        this.userService = userService;
        this.stockService = stockService;
    }

    @Override
    public List<Option> findAll() {
        return null;
    }

    @Override
    public Option save(Option option) {
        return null;
    }

    @Override
    public Optional<Option> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Option> findByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Option> findByStock(Long userId) {
        return null;
    }
}
