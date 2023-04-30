package rs.edu.raf.si.bank2.Bank2Backend.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.Permission;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.PermissionName;

public interface PermissionServiceInterface {
    List<Permission> findAll();

    List<Permission> findByPermissionNames(List<PermissionName> permissions);
}
