package rs.edu.raf.si.bank2.client.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.client.models.mariadb.Permission;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;

public interface PermissionServiceInterface {
    List<Permission> findAll();

    List<Permission> findByPermissionNames(List<PermissionName> permissions);
}
