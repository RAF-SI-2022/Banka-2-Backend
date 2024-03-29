package rs.edu.raf.si.bank2.otc.exceptions;

public class PasswordResetTokenNotFoundException extends RuntimeException {

    public PasswordResetTokenNotFoundException(String token) {
        super("Password reset token <" + token + "> not found.");
    }
}
