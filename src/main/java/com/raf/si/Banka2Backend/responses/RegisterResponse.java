package com.raf.si.Banka2Backend.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raf.si.Banka2Backend.models.users.Permission;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  @JsonIgnore private String password;
  private List<Permission> permissions;
  private String jobPosition;
  private boolean active;
  private String jmbg;

  private String phone;
}
