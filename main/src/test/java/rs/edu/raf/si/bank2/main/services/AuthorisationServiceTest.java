package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import rs.edu.raf.si.bank2.main.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.services.AuthorisationService;

@ExtendWith(MockitoExtension.class)
public class AuthorisationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    AuthorisationService authorisationService;

    @Test
    public void isAuthorised_success() {

        long id = 1L;

        PermissionName permission = PermissionName.ADMIN_USER;
        String email = "darko@gmail.com";

        User user = User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .permissions(Collections.singletonList(Permission.builder()
                        .permissionName(PermissionName.ADMIN_USER)
                        .build()))
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authorisationService.isAuthorised(permission, email);

        assertTrue(user.getPermissions().stream().anyMatch(perm -> permission.equals(perm.getPermissionName())));
    }

    @Test
    public void isAuthorized_failure() {
        long id = 1L;

        PermissionName permission = PermissionName.ADMIN_USER;
        String email = "darko@gmail.com";

        User user = User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .permissions(Collections.singletonList(Permission.builder()
                        .permissionName(PermissionName.CREATE_USERS)
                        .build()))
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authorisationService.isAuthorised(permission, email);

        assertFalse(user.getPermissions().stream().anyMatch(perm -> permission.equals(perm.getPermissionName())));
    }

    @Test
    public void isAuthorised_userNotFound() {

        PermissionName permission = PermissionName.ADMIN_USER;
        String email = "darko@gmail.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertFalse(authorisationService.isAuthorised(permission, email));
    }

    @Test
    public void validatePasswordResetToken_tokenNotFound() {

        String token = UUID.randomUUID().toString();
        String expectedMessage = "Token not found";

        when(passwordResetTokenRepository.findPasswordResetTokenByToken(token)).thenReturn(Optional.empty());

        assertEquals(expectedMessage, authorisationService.validatePasswordResetToken(token));
    }

    @Test
    public void validatePasswordResetToken_tokenExpired() {

        String token = UUID.randomUUID().toString();
        String expectedMessage = "Token expired";

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setExpirationDate(Date.from(LocalDateTime.now()
                .minus(Duration.ofMinutes(10))
                .atZone(ZoneId.systemDefault())
                .toInstant()));

        when(passwordResetTokenRepository.findPasswordResetTokenByToken(token))
                .thenReturn(Optional.of(passwordResetToken));

        assertEquals(expectedMessage, authorisationService.validatePasswordResetToken(token));

        verify(passwordResetTokenRepository).delete(passwordResetToken);
    }
}
