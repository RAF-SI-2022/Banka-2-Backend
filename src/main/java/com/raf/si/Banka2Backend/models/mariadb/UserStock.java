package com.raf.si.Banka2Backend.models.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
//@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "users_stocks")
public class UserStock {

    public UserStock(Long id, User user, Stock stock, Integer amount, Integer amountForSale) {
        this.id = id;
        this.user = user;
        this.stock = stock;
        this.amount = amount;
        this.amountForSale = amountForSale;
    }

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    @NotNull
    private Stock stock;

    @NotNull
    private Integer amount;

    @NotNull
    private Integer amountForSale;
}
