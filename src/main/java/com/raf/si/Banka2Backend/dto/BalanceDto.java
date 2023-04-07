package com.raf.si.Banka2Backend.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BalanceDto {
  @NotNull private String userEmail;
  @NotNull private String currencyCode;
  @NotNull private Float amount;
}
