package rs.edu.raf.si.bank2.users.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.models.mariadb.Permission;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.users.services.interfaces.PermissionServiceInterface;

@Service
public class PermissionService implements PermissionServiceInterface {
    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> findAll() {
        return this.permissionRepository.findAll();
    }

    @Override
    public List<Permission> findByPermissionNames(List<PermissionName> permissionNames) {
        return this.permissionRepository.findByPermissionNames(permissionNames);
    }
}
