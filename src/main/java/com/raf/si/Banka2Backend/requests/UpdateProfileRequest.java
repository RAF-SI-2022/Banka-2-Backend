package com.raf.si.Banka2Backend.requests;

import lombok.Data;

@Data
public class UpdateProfileRequest {

  private String email;
  private String firstName;
  private String lastName;
  private String phone;
}
