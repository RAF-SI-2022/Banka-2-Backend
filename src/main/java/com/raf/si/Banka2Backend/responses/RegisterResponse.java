package com.raf.si.Banka2Backend.responses;

import com.raf.si.Banka2Backend.models.Permission;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegisterResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Permission> permissions;
    private String pozicija;
    private boolean aktivan;
    private String jmbg;

}
