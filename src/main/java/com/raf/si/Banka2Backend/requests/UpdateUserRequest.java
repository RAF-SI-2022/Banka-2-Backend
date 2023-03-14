package com.raf.si.Banka2Backend.requests;
import com.raf.si.Banka2Backend.models.PermissionName;
import lombok.Data;

import java.util.List;

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