package com.raf.si.Banka2Backend.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raf.si.Banka2Backend.models.mariadb.Permission;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegisterResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private List<Permission> permissions;
    private String jobPosition;
    private boolean active;
    private String jmbg;
    private Double dailyLimit;
    private String phone;
}
