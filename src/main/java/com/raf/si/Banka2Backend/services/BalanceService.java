package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.BalanceNotFoundException;
import com.raf.si.Banka2Backend.exceptions.CurrencyNotFoundException;
import com.raf.si.Banka2Backend.exceptions.NotEnoughMoneyException;
import com.raf.si.Banka2Backend.exceptions.UserNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.repositories.mariadb.BalanceRepository;
import com.raf.si.Banka2Backend.services.interfaces.BalanceServiceInterface;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService implements BalanceServiceInterface {
    private final BalanceRepository balanceRepository;
    private final CurrencyService currencyService;

    private final UserService userService;

    @Autowired
    public BalanceService(
            BalanceRepository balanceRepository,
            CurrencyService currencyService,
            UserService userService) {
        this.balanceRepository = balanceRepository;
        this.currencyService = currencyService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void buyOrSellCurrency(
            String userEmail,
            String fromCurrencyCode,
            String toCurrencyCode,
            Float exchangeRate,
            Integer amountOfMoney) {
        Optional<Balance> balanceForFromCurrency =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(
                        userEmail, fromCurrencyCode);

        if (balanceForFromCurrency.isEmpty()) {
            throw new BalanceNotFoundException(userEmail, fromCurrencyCode);
        }
        if (balanceForFromCurrency.get().getAmount() < amountOfMoney) {
            throw new NotEnoughMoneyException(fromCurrencyCode, toCurrencyCode, amountOfMoney);
        }
        // Update existing balance(for fromCurrency)
        Float newAmountInFromCurrency = balanceForFromCurrency.get().getAmount() - amountOfMoney;
        balanceForFromCurrency.get().setAmount(newAmountInFromCurrency);
        this.balanceRepository.save(balanceForFromCurrency.get());

        // Check if balance for toCurrency exists. If yes update it with new amount, if not create it.
        Optional<Balance> balanceForToCurrency =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(
                        userEmail, toCurrencyCode);
        Optional<Currency> newCurrency = this.currencyService.findByCurrencyCode(toCurrencyCode);

        if (balanceForToCurrency.isPresent()) {
            // update existing balance for toCurrency
            Float newAmountInToCurrency =
                    balanceForToCurrency.get().getAmount() + amountOfMoney * exchangeRate;
            balanceForToCurrency.get().setAmount(newAmountInToCurrency);
            this.balanceRepository.save(balanceForToCurrency.get());
        } else {
            // create new balance for toCurrency
            Balance newBalanceForToCurrency = new Balance();
            newBalanceForToCurrency.setUser(this.userService.findByEmail(userEmail).get());
            newBalanceForToCurrency.setCurrency(newCurrency.get());
            newBalanceForToCurrency.setAmount(amountOfMoney * exchangeRate);
            this.balanceRepository.save(newBalanceForToCurrency);
        }
    }

    @Override
    public List<Balance> findAllByUserId(Long userId) {
        return this.balanceRepository.findAllByUser_Id(userId);
    }

    @Override
    public Balance findBalanceByUserIdAndCurrency(Long userId, String currencyCode) {
        Optional<Currency> currency = this.currencyService.findCurrencyByCurrencyCode(currencyCode);
        if (currency.isEmpty()) return null;

        Optional<Balance> balance =
                balanceRepository.findBalanceByUserIdAndCurrencyId(userId, currency.get().getId());
        if (balance.isPresent()) return balance.get();
        return null;
    }

    @Override
    public Balance increaseBalance(String userEmail, String currencyCode, Float amount)
            throws CurrencyNotFoundException, UserNotFoundException {
        Optional<Balance> balance =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(
                        userEmail, currencyCode);

        if (balance.isPresent()) {
            balance.get().setAmount(balance.get().getAmount() + amount);
            this.balanceRepository.save(balance.get());
            return balance.get();
        } else {
            Balance newBalance = new Balance();
            newBalance.setUser(this.userService.findByEmail(userEmail).get());
            newBalance.setCurrency(this.currencyService.findByCurrencyCode(currencyCode).get());
            newBalance.setAmount(amount);
            this.balanceRepository.save(newBalance);
            return newBalance;
        }
    }

    @Override
    public Balance decreaseBalance(String userEmail, String currencyCode, Float amount)
            throws BalanceNotFoundException, NotEnoughMoneyException {
        Optional<Balance> balance =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(
                        userEmail, currencyCode);
        if (balance.isEmpty()) {
            throw new BalanceNotFoundException(userEmail, currencyCode);
        }
        Float newAmount = balance.get().getAmount() - amount;
        if (newAmount < 0) {
            throw new NotEnoughMoneyException();
        }
        balance.get().setAmount(newAmount);
        this.balanceRepository.save(balance.get());
        return balance.get();
    }

    @Override
    public Balance save(Balance balance) {
        return balanceRepository.save(balance);
    }

    @Override
    public void exchangeMoney(
            String userFromEmail, String userToEmail, Float amount, String currencyCode) {
        decreaseBalance(userFromEmail, currencyCode, amount);
        increaseBalance(userToEmail, currencyCode, amount);
    }
}
