package com.raf.si.Banka2Backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.raf.si.Banka2Backend.models.mariadb.Permission;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.repositories.mariadb.PermissionRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

  @Mock PermissionRepository permissionRepository;

  @InjectMocks PermissionService permissionService;

  @Test
  public void getAllPermissions_success() {

    long id = 1L;
    List<Permission> permissionList =
        Arrays.asList(
            Permission.builder().id(id++).permissionName(PermissionName.ADMIN_USER).build(),
            Permission.builder().id(id++).permissionName(PermissionName.CREATE_USERS).build(),
            Permission.builder().id(id++).permissionName(PermissionName.DELETE_USERS).build(),
            Permission.builder().id(id++).permissionName(PermissionName.READ_USERS).build(),
            Permission.builder().id(id).permissionName(PermissionName.UPDATE_USERS).build());

    when(permissionRepository.findAll()).thenReturn(permissionList);

    final List<Permission> result = permissionService.findAll();
    assertIterableEquals(permissionList, result);
  }

  @Test
  public void findByPermissionNames_success() {

    long id = 1L;

    List<PermissionName> permissionNames =
        Arrays.asList(
            PermissionName.ADMIN_USER, PermissionName.READ_USERS, PermissionName.DELETE_USERS);

    List<Permission> permissionsByName =
        Arrays.asList(
            Permission.builder().id(id++).permissionName(PermissionName.ADMIN_USER).build(),
            Permission.builder().id(id++).permissionName(PermissionName.CREATE_USERS).build(),
            Permission.builder().id(id).permissionName(PermissionName.DELETE_USERS).build());

    when(permissionRepository.findByPermissionNames(permissionNames)).thenReturn(permissionsByName);

    final List<Permission> result = permissionService.findByPermissionNames(permissionNames);
    assertEquals(permissionsByName, result);
  }

  @Test
  public void findByPermissionNames_failure() {

    List<PermissionName> permissionNames =
        Arrays.asList(PermissionName.ADMIN_USER, PermissionName.READ_USERS);

    when(permissionRepository.findByPermissionNames(permissionNames)).thenReturn(null);

    List<Permission> result = permissionService.findByPermissionNames(permissionNames);

    assertNull(result);
  }
}
