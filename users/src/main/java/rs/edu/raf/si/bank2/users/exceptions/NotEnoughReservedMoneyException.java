package rs.edu.raf.si.bank2.users.exceptions;

public class NotEnoughReservedMoneyException extends RuntimeException {
    public NotEnoughReservedMoneyException(String message) {
        super(message);
    }
}
