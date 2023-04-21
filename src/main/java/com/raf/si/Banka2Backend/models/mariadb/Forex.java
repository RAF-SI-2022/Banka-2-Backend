package com.raf.si.Banka2Backend.models.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "forex")
public class Forex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fromCurrencyCode;

    @NotNull
    private String fromCurrencyName;

    @NotNull
    private String toCurrencyCode;

    @NotNull
    private String toCurrencyName;

    @NotNull
    private String exchangeRate;

    @NotNull
    private String lastRefreshed;

    @NotNull
    private String timeZone;

    @NotNull
    private String bidPrice;

    @NotNull
    private String askPrice;
}
