package com.raf.si.Banka2Backend.exceptions;

public class ExchangeNotFoundException extends RuntimeException {

    public ExchangeNotFoundException() {
        super("Requested exchange not found in the database.");
    }
}
