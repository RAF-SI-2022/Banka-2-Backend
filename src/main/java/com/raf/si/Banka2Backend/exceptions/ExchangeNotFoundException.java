package com.raf.si.Banka2Backend.exceptions;

public class ExchangeNotFoundException extends RuntimeException {

    public ExchangeNotFoundException() {
        System.out.println("Requested exchange not found in the database.");
    }
}
