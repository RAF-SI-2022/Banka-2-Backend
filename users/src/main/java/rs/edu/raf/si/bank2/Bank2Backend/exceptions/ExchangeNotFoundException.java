package rs.edu.raf.si.bank2.Bank2Backend.exceptions;

public class ExchangeNotFoundException extends RuntimeException {

    public ExchangeNotFoundException() {
        System.out.println("Requested exchange not found in the database.");
    }
}
