package com.raf.si.Banka2Backend.exceptions;

public class ExchangeNotFoundException extends RuntimeException{

    public ExchangeNotFoundException(String micCode){
        System.out.println("Exchange with MIC code "+ micCode + " not found.");
    }
}
