package com.raf.si.Banka2Backend.exceptions;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(Long id) {
        super("Stock with id <" + id + "> not found.");
    }

    public StockNotFoundException(String symbol) {
        super("Stock with symbol <" + symbol + "> not found.");
    }
}
