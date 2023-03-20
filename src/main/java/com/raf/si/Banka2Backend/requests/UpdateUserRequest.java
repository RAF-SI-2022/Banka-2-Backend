package com.raf.si.Banka2Backend.requests;

import com.raf.si.Banka2Backend.models.users.PermissionName;
import java.util.List;
import lombok.Data;

@Data
public class UpdateUserRequest {

  private String email;
  private String firstName;
  private String lastName;
  private List<PermissionName> permissions;
  private String jobPosition;
  private boolean active;
  private String phone;
}
