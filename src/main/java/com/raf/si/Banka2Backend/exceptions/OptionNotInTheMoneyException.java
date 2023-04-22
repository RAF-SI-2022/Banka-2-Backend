package com.raf.si.Banka2Backend.exceptions;

public class OptionNotInTheMoneyException extends RuntimeException{

    public OptionNotInTheMoneyException(Long optionId) {
        super("Option with id <" + optionId + "> is not 'In the money'.");
    }
}
