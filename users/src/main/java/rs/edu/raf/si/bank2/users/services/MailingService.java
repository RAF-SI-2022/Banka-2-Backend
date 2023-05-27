package rs.edu.raf.si.bank2.users.services;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;

@Service
public class MailingService {
    private static final String from = "banka2backend@gmail.com";
    private static final String password = "idxegskltunedxog";
    private UserRepository userRepo;
    private PasswordResetTokenRepository passwordResetTokenRepo;

    @Autowired
    public MailingService(UserRepository userRepo, PasswordResetTokenRepository passwordResetTokenRepo) {
        this.userRepo = userRepo;
        this.passwordResetTokenRepo = passwordResetTokenRepo;
    }

    public String sendResetPasswordMail(String recipient) {
        Optional<User> user = userRepo.findUserByEmail(recipient);
        if (user.isEmpty()) {
            return "user not found";
        }

        User client = user.get();

        try {
            String token = UUID.randomUUID().toString();
            passwordResetTokenRepo.save(new PasswordResetToken(client, token));
            this.sendMail(recipient, "Password Recovery", this.composeResetPasswordMail(token));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private String composeResetPasswordMail(String token) {
        return "To reset password please visit: http://localhost:4200/auth/change-password?token=" + token;
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

        email.setFrom(new InternetAddress(this.from));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        email.setSubject(subject);
        email.setText(content);

        Transport.send(email);
    }
}
