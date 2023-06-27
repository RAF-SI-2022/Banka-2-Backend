package rs.edu.raf.si.bank2.main.exceptions;

public class NotEnoughMoneyException extends RuntimeException {

    public NotEnoughMoneyException() {
        super("You don't have enough money for this operation.");
    }
}
