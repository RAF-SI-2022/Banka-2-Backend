package com.raf.si.Banka2Backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.raf.si.Banka2Backend.models.Permission;
import com.raf.si.Banka2Backend.models.PermissionName;
import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.repositories.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthorisationServiceTest {

  @Mock UserRepository userRepository;

  @InjectMocks AuthorisationService authorisationService;

  @Test
  public void isAuthorised_success() {

    long id = 1L;

    PermissionName permission = PermissionName.ADMIN_USER;
    String email = "darko@gmail.com";

    User user =
        User.builder()
            .id(id)
            .firstName("Darko")
            .lastName("Darkovic")
            .phone("000000000")
            .jmbg("000000000")
            .password("12345")
            .email("darko@gmail.com")
            .jobPosition("/")
            .permissions(
                Collections.singletonList(
                    Permission.builder().permissionName(PermissionName.ADMIN_USER).build()))
            .build();

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

    authorisationService.isAuthorised(permission, email);

    assertTrue(
        user.getPermissions().stream()
            .anyMatch(perm -> permission.equals(perm.getPermissionName())));
  }

  @Test
  public void isAuthorized_failure() {
    long id = 1L;

    PermissionName permission = PermissionName.ADMIN_USER;
    String email = "darko@gmail.com";

    User user =
        User.builder()
            .id(id)
            .firstName("Darko")
            .lastName("Darkovic")
            .phone("000000000")
            .jmbg("000000000")
            .password("12345")
            .email("darko@gmail.com")
            .jobPosition("/")
            .permissions(
                Collections.singletonList(
                    Permission.builder().permissionName(PermissionName.CREATE_USERS).build()))
            .build();

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

    authorisationService.isAuthorised(permission, email);

    assertFalse(
        user.getPermissions().stream()
            .anyMatch(perm -> permission.equals(perm.getPermissionName())));
  }

  @Test
  public void isAuthorised_notFound() {

    PermissionName permission = PermissionName.ADMIN_USER;
    String email = "darko@gmail.com";

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

    assertFalse(authorisationService.isAuthorised(permission, email));
  }
}
