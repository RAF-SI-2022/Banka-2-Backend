package rs.edu.raf.si.bank2.users.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("User with id <" + id + "> not found.");
    }

    public UserNotFoundException(String email) {
        super("User with email <" + email + "> not found.");
    }
}
