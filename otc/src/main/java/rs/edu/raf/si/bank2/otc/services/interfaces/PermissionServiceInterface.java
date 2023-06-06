package rs.edu.raf.si.bank2.otc.services.interfaces;

import rs.edu.raf.si.bank2.otc.models.mariadb.Permission;
import rs.edu.raf.si.bank2.otc.models.mariadb.PermissionName;

import java.util.List;

public interface PermissionServiceInterface {
    List<Permission> findAll();

    List<Permission> findByPermissionNames(List<PermissionName> permissions);
}
