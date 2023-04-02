package com.raf.si.Banka2Backend.exceptions;

public class BalanceNotFoundException extends RuntimeException {

  public BalanceNotFoundException(long id) {
    super("Balance with id <" + id + "> not found.");
  }

  public BalanceNotFoundException(String userEmail, String currencyCode) {
    super(
        "Balance for user with email <"
            + userEmail
            + "> and currencyCode <"
            + currencyCode
            + "> not found.");
  }
}
