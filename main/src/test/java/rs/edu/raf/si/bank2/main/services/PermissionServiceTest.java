package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PermissionRepository;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    PermissionRepository permissionRepository;

    @InjectMocks
    PermissionService permissionService;

    @Test
    public void getAllPermissions_success() {

        long id = 1L;
        List<Permission> permissionList = Arrays.asList(
                Permission.builder()
                        .id(id++)
                        .permissionName(PermissionName.ADMIN_USER)
                        .build(),
                Permission.builder()
                        .id(id++)
                        .permissionName(PermissionName.CREATE_USERS)
                        .build(),
                Permission.builder()
                        .id(id++)
                        .permissionName(PermissionName.DELETE_USERS)
                        .build(),
                Permission.builder()
                        .id(id++)
                        .permissionName(PermissionName.READ_USERS)
                        .build(),
                Permission.builder()
                        .id(id)
                        .permissionName(PermissionName.UPDATE_USERS)
                        .build());

        when(permissionRepository.findAll()).thenReturn(permissionList);

        final List<Permission> result = permissionService.findAll();
        assertIterableEquals(permissionList, result);
    }

    @Test
    public void findByPermissionNames_success() {

        long id = 1L;

        List<PermissionName> permissionNames =
                Arrays.asList(PermissionName.ADMIN_USER, PermissionName.READ_USERS, PermissionName.DELETE_USERS);

        List<Permission> permissionsByName = Arrays.asList(
                Permission.builder()
                        .id(id++)
                        .permissionName(PermissionName.ADMIN_USER)
                        .build(),
                Permission.builder()
                        .id(id++)
                        .permissionName(PermissionName.CREATE_USERS)
                        .build(),
                Permission.builder()
                        .id(id)
                        .permissionName(PermissionName.DELETE_USERS)
                        .build());

        when(permissionRepository.findByPermissionNames(permissionNames)).thenReturn(permissionsByName);

        final List<Permission> result = permissionService.findByPermissionNames(permissionNames);
        assertEquals(permissionsByName, result);
    }

    @Test
    public void findByPermissionNames_failure() {

        List<PermissionName> permissionNames = Arrays.asList(PermissionName.ADMIN_USER, PermissionName.READ_USERS);

        when(permissionRepository.findByPermissionNames(permissionNames)).thenReturn(null);

        List<Permission> result = permissionService.findByPermissionNames(permissionNames);

        assertNull(result);
    }
}
