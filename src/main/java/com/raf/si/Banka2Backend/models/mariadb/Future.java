package com.raf.si.Banka2Backend.models.mariadb;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
      String type,
      String settlementDate) { // open contract
    this.futureName = futureName;
    this.contractSize = contractSize;
    this.contractUnit = contractUnit;
    this.maintenanceMargin = maintenanceMargin;
    this.type = type;
    this.settlementDate = settlementDate;
  }

  public Future(Future future) { // za randomizaciju u bootstrap
    this.futureName = future.futureName;
    this.contractSize = future.contractSize;
    this.contractUnit = future.contractUnit;
    this.maintenanceMargin = future.maintenanceMargin;
    this.type = future.type;
    this.settlementDate = future.settlementDate;
  }

  @NotNull private String futureName;

  @NotNull private Integer contractSize;

  @NotNull private String contractUnit;

  @NotNull private Integer maintenanceMargin;

  @NotNull private String type;

  private String settlementDate;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

}
