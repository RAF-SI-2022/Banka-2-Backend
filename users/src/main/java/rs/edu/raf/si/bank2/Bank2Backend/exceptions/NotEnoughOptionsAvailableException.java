package rs.edu.raf.si.bank2.Bank2Backend.exceptions;

public class NotEnoughOptionsAvailableException extends RuntimeException {
    public NotEnoughOptionsAvailableException(Integer available, Integer wanted) {
        super("Available option for sale <" + available + "> but <" + wanted + "> wanted.");
    }
}
