package com.raf.si.Banka2Backend.models.mariadb;

import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "options")
public class Option {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String stockSymbol;

    @NotNull
    private String contractSymbol;

    @NotNull
    private String optionType;

    @NotNull
    private Double strike;

    @NotNull
    private Double impliedVolatility;

    @NotNull
    private Double price;

    @NotNull
    private LocalDate expirationDate;

    @NotNull
    private Integer openInterest;

    @NotNull
    private Integer contractSize;

    @NotNull
    private Double maintenanceMargin;

    @NotNull
    private Double bid;

    @NotNull
    private Double ask;

    @NotNull
    private Double changePrice;

    @NotNull
    private Double percentChange;

    @NotNull
    private Boolean inTheMoney;

    // todo theta

}
