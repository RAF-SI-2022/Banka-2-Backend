package com.raf.si.Banka2Backend.models.mariadb;

import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(
        name = "currencies",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})}) // dodaj posle i ostale atribute
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currencyName;
    private String currencyCode;
    private String currencySymbol;
    private String polity;

    @OneToMany(mappedBy = "currency")
    private List<Inflation> inflations;
}
