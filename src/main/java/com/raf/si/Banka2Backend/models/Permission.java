package com.raf.si.Banka2Backend.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {@UniqueConstraint(columnNames = "permissionName")})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String permissionName;

    @ManyToMany(mappedBy = "permissions")
    private Collection<User> users;
}

