package com.raf.si.Banka2Backend.requests;

import com.raf.si.Banka2Backend.models.Permission;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Permission> permissions;
    private String pozicija;
    private boolean aktivan;
    private String jmbg;
}
