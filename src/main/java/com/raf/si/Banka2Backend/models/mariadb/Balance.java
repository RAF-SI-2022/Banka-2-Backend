package com.raf.si.Banka2Backend.models.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@ToString(exclude = {"user", "currency"})
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(
        name = "balances",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BalanceType type;

    @NotNull
    private Float amount;

    @NotNull
    private Float reserved;

    @NotNull
    private Float free;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    @NotNull
    private Currency currency;
}
