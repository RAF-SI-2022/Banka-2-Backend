package rs.edu.raf.si.bank2.client.services.interfaces;

/**
 * Service for sending transactional emails.
 */
public interface MailingServiceInterface {


    void sendRegistrationToken(String email);

    boolean checkIfTokenGood(String token);
}
