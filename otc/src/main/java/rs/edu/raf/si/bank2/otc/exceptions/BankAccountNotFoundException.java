package rs.edu.raf.si.bank2.otc.exceptions;

public class BankAccountNotFoundException extends RuntimeException{

    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
