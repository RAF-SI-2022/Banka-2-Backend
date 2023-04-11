package com.raf.si.Banka2Backend.models.mariadb;

import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
// @Builder
@AllArgsConstructor
// @RequiredArgsConstructor
// @NoArgsConstructor
@Entity
@Table(
        name = "exchange",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"acronym", "micCode"}),
        })
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Exchange() {
    }

    public Exchange(
            String exchangeName,
            String acronym,
            String micCode,
            String polity,
            Currency currency,
            String timeZone,
            String openTime,
            String closeTime) {
        this.exchangeName = exchangeName;
        this.acronym = acronym;
        this.micCode = micCode;
        this.polity = polity;
        this.currency = currency;
        this.timeZone = timeZone;
        this.openTime = openTime;
        this.closeTime = closeTime;
        //        this.calendar = calendar;
    }

    private String exchangeName;

    private String acronym;

    @NotNull
    private String micCode;

    private String polity;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    private String timeZone;

    private String openTime;

    private String closeTime;

    @ElementCollection
    @CollectionTable(name = "exchange_calendar", joinColumns = @JoinColumn(name = "exchange_id"))
    @Column(name = "calendar_value")
    private Collection<String> calendar;
}
