package com.raf.si.Banka2Backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BuySellForexDto {
    /**
     * base currency
     */
    @NotNull String fromCurrencyCode;
    /**
     * quote currency
     */
    @NotNull String toCurrencyCode;
    /**
     * Pri kupovini: koliko korisnik zeli da potrosi na kupovinu (a ne koliko zeli da kupi druge
     * valute). Pri prodaji: koliko korisnik zeli da proda od trenutne valute koju ima
     */
    @NotNull Integer amountOfMoney;
}
