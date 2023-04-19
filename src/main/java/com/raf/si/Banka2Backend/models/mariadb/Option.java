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
@Table(name = "options")
public class Option {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String contractSymbol;

    @NotNull
    private String optionType;

    @NotNull
    private Double strike;

    @NotNull
    private Double impliedVolatility;

    @NotNull
    private String expirationDate;

    @NotNull
    private Integer openInterest;

    @NotNull
    private Integer contractSize;

    @NotNull
    private Double maintenanceMargin;

    //todo theta

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
