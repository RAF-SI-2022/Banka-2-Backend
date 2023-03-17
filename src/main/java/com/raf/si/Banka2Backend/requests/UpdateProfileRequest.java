package com.raf.si.Banka2Backend.requests;

import com.raf.si.Banka2Backend.models.PermissionName;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}