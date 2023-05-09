package rs.edu.raf.si.bank2.main.exceptions;

public class ExchangeNotFoundException extends RuntimeException {

    public ExchangeNotFoundException() {
        super("Requested exchange not found in the database.");
    }
}
