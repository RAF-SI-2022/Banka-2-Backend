package com.raf.si.Banka2Backend.models.mariadb;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
// @RequiredArgsConstructor
@Entity
@Table(name = "futureTable")
public class Future {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Future() {}

  public Future(
      String futureName,
      Integer contractSize,
      String contractUnit,
      Integer maintenanceMargin,
      String settlementDate,
      Boolean openFuture) { // open contract
    this.futureName = futureName;
    this.contractSize = contractSize;
    this.contractUnit = contractUnit;
    this.maintenanceMargin = maintenanceMargin;
    this.settlementDate = settlementDate;
    this.openFuture = openFuture;
  }

  @NotNull private String futureName;

  @NotNull private Integer contractSize;

  @NotNull private String contractUnit;

  @NotNull private Integer maintenanceMargin;

  private String settlementDate;

  private Boolean openFuture;
}
