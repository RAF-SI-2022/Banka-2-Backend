package com.raf.si.Banka2Backend.dto;

import lombok.Data;

@Data
public class PasswordRecoveryDto {
  private String token;
  private String newPassword;
}
