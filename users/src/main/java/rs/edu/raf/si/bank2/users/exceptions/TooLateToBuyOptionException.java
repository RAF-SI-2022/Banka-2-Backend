package rs.edu.raf.si.bank2.users.exceptions;

import java.time.LocalDate;

public class TooLateToBuyOptionException extends RuntimeException {

    public TooLateToBuyOptionException(LocalDate date, Long optionId) {
        super("Option with id <" + optionId + "> cannot be bought because its expiration date is <" + date + ">");
    }
}
