package rs.edu.raf.si.bank2.main.exceptions;

public class BalanceNotFoundException extends RuntimeException {

    public BalanceNotFoundException(String userEmail, String currencyCode) {
        super("Balance for user with email <" + userEmail + "> and currencyCode <" + currencyCode + "> not found.");
    }
}
