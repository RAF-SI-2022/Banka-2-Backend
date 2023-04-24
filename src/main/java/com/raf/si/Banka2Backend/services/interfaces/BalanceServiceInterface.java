package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import java.util.List;

public interface BalanceServiceInterface {
    void buyOrSellCurrency(
            String userEmail,
            String fromCurrencyCode,
            String toCurrencyCode,
            Float exchangeRate,
            Integer amountOfMoney);

    List<Balance> findAllByUserId(Long userId);

    Balance increaseBalance(String userEmail, String currencyCode, Float amount);

    Balance decreaseBalance(String userEmail, String currencyCode, Float amount);

    Balance save(Balance balance);

    void exchangeMoney(String userFromEmail, String userToEmail, Float amount, String currencyCode);

    Balance findBalanceByUserIdAndCurrency(Long userId, String currency);

    Balance findBalanceByUserEmailAndCurrencyCode(String userEmail, String currencyCode);

    Balance reserveAmount(Float amount, String userEmail, String currencyCode);

    /**
     * Pre pocetka svake kupovine pozvati ovu metodu, ako se vrati true, pozvati metodu reserveAmount.
     */
    boolean hasEnoughFounds(Float amountNeeded, String userEmail, String currencyCode);

    /**
     * Kada se izvrse sve transakcije iz ordera, order sa stavlja u status complete i tada treba da se zove ova metoda updateBalance.
     */
    Balance updateBalance(Order order, String userEmail, String currencyCode);
}
