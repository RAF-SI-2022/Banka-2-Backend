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

    @NotNull Integer amount;
}
