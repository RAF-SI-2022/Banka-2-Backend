package rs.edu.raf.si.bank2.users.exceptions;

public class ExchangeNotFoundException extends RuntimeException {

    public ExchangeNotFoundException() {
        super("Requested exchange not found in the database.");
    }
}
