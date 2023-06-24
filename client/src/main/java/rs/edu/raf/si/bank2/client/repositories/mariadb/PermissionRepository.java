package rs.edu.raf.si.bank2.client.repositories.mariadb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mariadb.Permission;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(PermissionName permissionName);

    @Query("SELECT p FROM Permission p WHERE p.permissionName IN :permissionNames")
    List<Permission> findByPermissionNames(List<PermissionName> permissionNames);
}
