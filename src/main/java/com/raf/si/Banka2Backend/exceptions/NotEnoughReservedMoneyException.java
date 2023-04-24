package com.raf.si.Banka2Backend.exceptions;

public class NotEnoughReservedMoneyException extends RuntimeException {
    public NotEnoughReservedMoneyException(String message) {
        super(message);
    }
}
