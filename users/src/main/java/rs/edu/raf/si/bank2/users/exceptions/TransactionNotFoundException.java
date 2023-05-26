package rs.edu.raf.si.bank2.users.exceptions;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction with id <" + id + "> , has not been found.");
    }
}
