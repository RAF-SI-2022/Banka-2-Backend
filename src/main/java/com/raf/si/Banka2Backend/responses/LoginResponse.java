package com.raf.si.Banka2Backend.responses;

import com.raf.si.Banka2Backend.models.Permission;
import java.util.List;
import lombok.Data;

@Data
public class LoginResponse {
  private String token;
  private List<Permission> permissions;

  public LoginResponse(String token, List<Permission> permissions) {
    this.token = token;
    this.permissions = permissions;
  }
}
