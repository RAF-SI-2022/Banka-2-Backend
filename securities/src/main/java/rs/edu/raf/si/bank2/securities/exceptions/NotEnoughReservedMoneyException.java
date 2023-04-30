package rs.edu.raf.si.bank2.securities.exceptions;

public class NotEnoughReservedMoneyException extends RuntimeException {
    public NotEnoughReservedMoneyException(String message) {
        super(message);
    }
}
