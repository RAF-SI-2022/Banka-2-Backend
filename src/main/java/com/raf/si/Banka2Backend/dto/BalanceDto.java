package com.raf.si.Banka2Backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BalanceDto {
    @NotNull
    private String userEmail;
    @NotNull
    private String currencyCode;
    @NotNull
    private Float amount;
}
