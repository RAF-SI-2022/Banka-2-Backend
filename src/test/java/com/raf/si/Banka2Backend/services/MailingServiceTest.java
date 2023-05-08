package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.PasswordResetToken;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.repositories.mariadb.PasswordResetTokenRepository;
import com.raf.si.Banka2Backend.repositories.mariadb.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(mockUserRepo.findUserByEmail("recipient")).thenReturn(Optional.of(User.builder().build()));

        // Run the test
        final String result = mailingServiceUnderTest.sendResetPasswordMail("recipient");

        // Verify the results
        assertEquals("", result);

        // Confirm PasswordResetTokenRepository.save(...).
        final PasswordResetToken entity = new PasswordResetToken();
        entity.setId(0L);
        entity.setToken("token");
        entity.setUser(User.builder().build());
        entity.setExpirationDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        verify(mockPasswordResetTokenRepo).save(entity);
    }

    @Test
    void testSendResetPasswordMail_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepo.findUserByEmail("recipient")).thenReturn(Optional.empty());

        // Run the test
        final String result = mailingServiceUnderTest.sendResetPasswordMail("recipient");

        // Verify the results
        assertEquals("", result);
    }
}
