package com.raf.si.Banka2Backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InflationDto {
    @NotNull
    private Integer year;
    @NotNull
    private Float inflationRate;
    @NotNull
    private Long currencyId;
}
