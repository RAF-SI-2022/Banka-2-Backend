package com.raf.si.Banka2Backend.repositories.users;

import com.raf.si.Banka2Backend.models.users.Permission;
import com.raf.si.Banka2Backend.models.users.PermissionName;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  Optional<Permission> findByPermissionName(PermissionName permissionName);

  @Query("SELECT p FROM Permission p WHERE p.permissionName IN :permissionNames")
  List<Permission> findByPermissionNames(List<PermissionName> permissionNames);
}
