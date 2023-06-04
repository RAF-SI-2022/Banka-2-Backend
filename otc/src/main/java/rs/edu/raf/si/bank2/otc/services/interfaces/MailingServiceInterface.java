package rs.edu.raf.si.bank2.otc.services.interfaces;

/**
 * Service for sending transactional emails.
 */
public interface MailingServiceInterface {

    /**
     * Sends the password reset link to the specified email address. Should be
     * used after resetting a user's password. Typically, the link will
     * contain a password reset token.
     *
     * @param email the user's email address (recipient)
     * @param link  the password reset link
     */
    void sendResetPasswordEmail(String email, String link);
}
