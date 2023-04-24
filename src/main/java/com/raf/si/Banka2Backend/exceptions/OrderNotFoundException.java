package com.raf.si.Banka2Backend.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order with id <" + id + "> has not been found.");
    }
}
