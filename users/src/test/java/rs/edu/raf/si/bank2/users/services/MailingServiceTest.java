package rs.edu.raf.si.bank2.users.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class MailingServiceTest {

    private MailingService mailingServiceUnderTest;

    @BeforeEach
    void setUp() {
        mailingServiceUnderTest = new MailingService();
    }

    @Test
    void testSendResetPasswordMail() {
        // Run the test
        String email = "anesic3119rn+banka2backend+test@raf.rs";
        String link = "https://youtu.be/xvFZjo5PgG0";
        try (MockedStatic<Transport> transport = Mockito.mockStatic(Transport.class)) {
            transport
                    .when(() -> Transport.send(Mockito.any(javax.mail.Message.class)))
                    .thenAnswer((Answer<Void>) invocation -> null);
            assertDoesNotThrow(() -> mailingServiceUnderTest.sendResetPasswordEmail(email, link));
        }

        // TODO sve ovo ispod sto pise treba da se radi sa authorisation
        //  service, ne za mailingservice. mailingservice samo salje, nema
        //  potrebe za biznis logikom u njemu.
        // Verify the results
        // LocalDateTime expiration = LocalDateTime.now().plus(Duration
        // .ofMinutes(15));
        // Date expirationDate = Date.from(expiration.atZone(ZoneId
        // .systemDefault()).toInstant());
        // // Confirm PasswordResetTokenRepository.save(...).
        // final PasswordResetToken entity = new PasswordResetToken();
        // entity.setId(0L);
        // entity.setToken("token");
        // entity.setUser(User.builder().build());
        // entity.setExpirationDate(expirationDate);
        // verify(mockPasswordResetTokenRepo).save(entity);

        // nista od ovog verifija ne moze jer je token randomly generisan a i
        // expiration
        // date ne moze da se matchuje cak i ako dodam tacno 15 minuta koliko
        // je expiration time
        // za password reset token jer su razlicita vremena izvrsavanja te
        // dve linije koda
    }
}
