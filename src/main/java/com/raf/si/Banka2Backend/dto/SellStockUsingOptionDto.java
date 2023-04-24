package com.raf.si.Banka2Backend.dto;

import lombok.Data;

@Data
public class SellStockUsingOptionDto {
    private String stockSymbol;
    private Integer optionAmount;
    private Double strikePrice;
}
