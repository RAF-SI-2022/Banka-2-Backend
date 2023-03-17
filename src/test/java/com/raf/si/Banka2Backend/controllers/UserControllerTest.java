package com.raf.si.Banka2Backend.controllers;

// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
// import static org.mockito.Mockito.when;
//
// import com.raf.si.Banka2Backend.controllers.UserController;
// import com.raf.si.Banka2Backend.models.Permission;
// import com.raf.si.Banka2Backend.models.PermissionName;
// import com.raf.si.Banka2Backend.services.AuthorisationService;
// import com.raf.si.Banka2Backend.services.PermissionService;
// import com.raf.si.Banka2Backend.services.UserService;
// import java.util.Arrays;
// import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  //      @Autowired
  //      MockMvc mvc;
  //      @Mock
  //      UserService userService;
  //
  //      @Mock
  //      PermissionService permissionService;
  //
  //      @Mock
  //      AuthorisationService authorisationService;
  //
  //      @Mock
  //      PasswordEncoder passwordEncoder;
  //
  //      @InjectMocks
  //      UserController userController;

  @Test
  public void getAllPermissions_success() {

    //        Authentication authentication = Mockito.mock(Authentication.class);
    //
    //        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    //        securityContext.setAuthentication(authentication);
    //        SecurityContextHolder.setContext(securityContext);
    //
    //        long id = 1L;
    //
    //        PermissionName permissionName = PermissionName.ADMIN_USER;
    //        String signedInUserEmail = "darko@gmail.com";
    //
    //        List<Permission> permissions =
    //            Arrays.asList(
    //
    // Permission.builder().id(id++).permissionName(PermissionName.ADMIN_USER).build(),
    //
    // Permission.builder().id(id++).permissionName(PermissionName.CREATE_USERS).build(),
    //
    // Permission.builder().id(id++).permissionName(PermissionName.READ_USERS).build(),
    //
    // Permission.builder().id(id++).permissionName(PermissionName.DELETE_USERS).build(),
    //
    // Permission.builder().id(id).permissionName(PermissionName.UPDATE_USERS).build());
    //
    //        when(securityContext.getAuthentication()).thenReturn(authentication);
    //        when(authorisationService.isAuthorised(permissionName,
    //     signedInUserEmail)).thenReturn(true);
    //        when(permissionService.findAll()).thenReturn(permissions);
    //
    //        ResponseEntity<?> result = userController.getAllPermissions();
    //
    //        assertThat(result.getStatusCodeValue()).isEqualTo(200);
  }
}
