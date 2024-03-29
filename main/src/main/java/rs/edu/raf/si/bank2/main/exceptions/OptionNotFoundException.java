package rs.edu.raf.si.bank2.main.exceptions;

public class OptionNotFoundException extends RuntimeException {

    public OptionNotFoundException(Long optionId) {
        super("Option with id <" + optionId + "> not found.");
    }
}
