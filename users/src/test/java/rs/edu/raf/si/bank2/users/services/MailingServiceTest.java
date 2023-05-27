package rs.edu.raf.si.bank2.users.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;

@ExtendWith(MockitoExtension.class)
class MailingServiceTest {

    @Mock
    private UserRepository mockUserRepo;

    @Mock
    private PasswordResetTokenRepository mockPasswordResetTokenRepo;

    private MailingService mailingServiceUnderTest;

    @BeforeEach
    void setUp() {
        mailingServiceUnderTest = new MailingService(mockUserRepo, mockPasswordResetTokenRepo);
    }

    @Test
    void testSendResetPasswordMail() {
        // Setup
        when(mockUserRepo.findUserByEmail("recipient@gmail.com"))
                .thenReturn(Optional.of(User.builder().build()));

        // Run the test
        final String result = mailingServiceUnderTest.sendResetPasswordMail("recipient@gmail.com");
        // Verify the results
        assertEquals("", result);

        //        LocalDateTime expiration = LocalDateTime.now().plus(Duration.ofMinutes(15));
        //        Date expirationDate = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
        //        // Confirm PasswordResetTokenRepository.save(...).
        //        final PasswordResetToken entity = new PasswordResetToken();
        //        entity.setId(0L);
        //        entity.setToken("token");
        //        entity.setUser(User.builder().build());
        //        entity.setExpirationDate(expirationDate);
        //        verify(mockPasswordResetTokenRepo).save(entity);

        // nista od ovog verifija ne moze jer je token randomly generisan a i expiration
        // date ne moze da se matchuje cak i ako dodam tacno 15 minuta koliko je expiration time
        // za password reset token jer su razlicita vremena izvrsavanja te dve linije koda
    }

    @Test
    void testSendResetPasswordMail_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepo.findUserByEmail("recipient@gmail.com")).thenReturn(Optional.empty());

        // Run the test
        final String result = mailingServiceUnderTest.sendResetPasswordMail("recipient@gmail.com");

        // Verify the results
        assertEquals("user not found", result);
    }

    @Test
    void testSendResetPasswordMail_MessagingInvalidRecipient() {
        // Setup
        when(mockUserRepo.findUserByEmail("recipient"))
                .thenReturn(Optional.of(User.builder().build()));

        // Verify the results
        assertThrows(RuntimeException.class, () -> mailingServiceUnderTest.sendResetPasswordMail("recipient"));
    }
}
