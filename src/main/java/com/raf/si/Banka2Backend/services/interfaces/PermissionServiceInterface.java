package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.Permission;
import com.raf.si.Banka2Backend.models.PermissionName;

import java.util.List;

public interface PermissionServiceInterface {
    List<Permission> findAll();

    List<Permission> findByPermissionNames(List<PermissionName> permissions);
}
