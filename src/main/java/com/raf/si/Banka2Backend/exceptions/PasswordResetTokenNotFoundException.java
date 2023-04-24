package com.raf.si.Banka2Backend.exceptions;

public class PasswordResetTokenNotFoundException extends RuntimeException {

    public PasswordResetTokenNotFoundException(String token) {
        super("Password reset token <" + token + "> not found.");
    }
}
