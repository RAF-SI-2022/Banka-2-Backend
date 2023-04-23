package com.raf.si.Banka2Backend.exceptions;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction with id <" + id + "> , has not been found.");
    }
}
