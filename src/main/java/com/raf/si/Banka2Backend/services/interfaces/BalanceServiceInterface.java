package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.User;

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
}
