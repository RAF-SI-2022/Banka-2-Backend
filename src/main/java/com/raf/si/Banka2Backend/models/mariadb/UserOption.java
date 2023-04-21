package com.raf.si.Banka2Backend.models.mariadb;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@RequiredArgsConstructor
@Entity
@Table(name = "users_options")
public class UserOption {

    public UserOption(Long id, User user, Option option, Double userPrice, Integer amount) {
        this.id = id;
        this.user = user;
        this.option = option;
        this.userPrice = userPrice;
        this.amount = amount;
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
    private Option option;

    @NotNull
    private Double userPrice;

    @NotNull
    private Integer amount;
}
