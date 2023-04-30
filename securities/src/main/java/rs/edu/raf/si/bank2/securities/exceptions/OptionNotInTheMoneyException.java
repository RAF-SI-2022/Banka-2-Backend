package rs.edu.raf.si.bank2.securities.exceptions;

public class OptionNotInTheMoneyException extends RuntimeException {

    public OptionNotInTheMoneyException(Long optionId) {
        super("Option with id <" + optionId + "> is not 'In the money'.");
    }
}
