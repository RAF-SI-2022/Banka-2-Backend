package rs.edu.raf.si.bank2.securities.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.securities.models.mariadb.Permission;
import rs.edu.raf.si.bank2.securities.models.mariadb.PermissionName;

public interface PermissionServiceInterface {
    List<Permission> findAll();

    List<Permission> findByPermissionNames(List<PermissionName> permissions);
}
