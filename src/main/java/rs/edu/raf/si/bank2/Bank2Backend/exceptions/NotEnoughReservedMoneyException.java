package rs.edu.raf.si.bank2.Bank2Backend.exceptions;

public class NotEnoughReservedMoneyException extends RuntimeException {
    public NotEnoughReservedMoneyException(String message) {
        super(message);
    }
}
