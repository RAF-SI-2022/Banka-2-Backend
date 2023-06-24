package rs.edu.raf.si.bank2.users.services;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.services.interfaces.MailingServiceInterface;

@Timed
@Service
public class MailingService implements MailingServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(MailingService.class);
    private static final String from = "banka2backend@gmail.com";
    private static final String password = "idxegskltunedxog";

    /**
     * Monitoring. Keeps track of the number of emails sent.
     */
    private Counter sent;

    @Autowired
    public MailingService(
            CompositeMeterRegistry meterRegistry
    ) {
        sent = meterRegistry.counter("services.mailing.sent");
    }

    @Timed("services.mailing.sendMail")
    private void sendMail(String recipient, String subject, String content) throws MessagingException {
        // Setting up STMP server
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        email.setSubject(subject);
        email.setText(content);

        Transport.send(email);
        sent.increment();

        // TODO: da li ovde treba nesto da se zatvori? npr session ili
        //  transport?
    }

    @Override
    public void sendResetPasswordEmail(String email, String link) {
        String template =
                """
                Hello,

                A password reset was requested for your account. To reset your password, please visit the following link:

                %s

                If you did not request a password reset, please ignore this email.
                """;
        try {
            sendMail(email, "Password Reset", String.format(template, link));
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email", e);
        }
    }
}
