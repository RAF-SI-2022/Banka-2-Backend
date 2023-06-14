package rs.edu.raf.si.bank2.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.services.interfaces.MailingServiceInterface;

@Service
public class MailingService implements MailingServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(MailingService.class);
    private static final String from = "banka2backend@gmail.com";
    private static final String password = "idxegskltunedxog";
    private final List<String> registrationCodes;

    @Autowired
    public MailingService() {
        this.registrationCodes = new ArrayList<>();
    }

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

        // TODO: da li ovde treba nesto da se zatvori? npr session ili
        //  transport?
    }

    @Override
    public void sendRegistrationToken(String email) {
        String template =
                """
                Hello,

                This is your registration token which is used for setting up your password for the first time.

                TOKEN: %s

                If you did not request a password reset, please ignore this email.
                """;
        try {
            int randomCodePlace = new Random().nextInt(registrationCodes.size()) + 1;
            sendMail(email, "Registration token", String.format(template, registrationCodes.get(randomCodePlace)));
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email", e);
        }
    }

    @Override
    public boolean checkIfTokenGood(String token) {
        return registrationCodes.contains(token);
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

    public List<String> getRegistrationCodes() {
        return registrationCodes;
    }
}
