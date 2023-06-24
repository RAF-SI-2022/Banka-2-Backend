package rs.edu.raf.si.bank2.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import rs.edu.raf.si.bank2.users.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.users.models.mariadb.Permission;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;
import rs.edu.raf.si.bank2.users.services.interfaces.MailingServiceInterface;
import rs.edu.raf.si.bank2.users.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthorisationServiceTest {

    @Mock
    CompositeMeterRegistry meterRegistry = new CompositeMeterRegistry();

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    MailingServiceInterface mailingService;

    @Mock
    JwtUtil jwtUtil;

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
    public void login_success() {
        String user = "test";
        String pass = "test";
        Authentication auth = new UsernamePasswordAuthenticationToken(user, pass);

        when(authenticationManager.authenticate(auth)).thenReturn(null);
        when(jwtUtil.generateToken(any())).thenReturn("token");
        Optional<String> res = authorisationService.login(user, pass);
        assertTrue(res.isPresent());
    }

    @Test
    public void login_fail() {
        String user = "test";
        String pass = "test";
        Authentication auth = new UsernamePasswordAuthenticationToken(user, pass);

        String err = "Throwable for bad credentials";
        AuthenticationException ae = new AuthenticationException(err, new Throwable()) {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        doThrow(ae).when(authenticationManager).authenticate(any());
        Optional<String> res = authorisationService.login(user, pass);
        assertTrue(res.isEmpty());
    }

    @Test
    public void requestPasswordResetToken_success() {
        String email = "email";
        User user = User.builder()
                .id(1L)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email(email)
                .jobPosition("/")
                .permissions(Collections.singletonList(Permission.builder()
                        .permissionName(PermissionName.CREATE_USERS)
                        .build()))
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenReturn(null);
        doNothing().when(mailingService).sendResetPasswordEmail(any(), any());

        assertTrue(authorisationService.requestPasswordResetToken(email));
    }

    @Test
    public void requestPasswordResetToken_failInvalidUser() {
        String email = "email";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        assertFalse(authorisationService.requestPasswordResetToken(email));
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
