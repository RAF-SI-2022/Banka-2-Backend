package com.raf.si.Banka2Backend.exceptions;

public class CurrencyNotFoundException extends RuntimeException {

  public CurrencyNotFoundException(long id) {
    super("Currency with id <" + id + "> not found.");
  }

  public CurrencyNotFoundException(String name) {
    super("Currency <" + name + "> not found.");
  }
}
