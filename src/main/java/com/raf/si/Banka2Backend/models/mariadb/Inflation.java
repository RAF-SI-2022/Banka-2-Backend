package com.raf.si.Banka2Backend.models.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    name = "inflations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})}) // dodaj posle i currency?
public class Inflation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer year;
  private Float inflationRate;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinTable(
      name = "currencies_inflations",
      joinColumns = {@JoinColumn(name = "inflation_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "currency_id", referencedColumnName = "id")})
  private Currency currency;
}
