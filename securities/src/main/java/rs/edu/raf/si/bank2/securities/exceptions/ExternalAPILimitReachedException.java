package rs.edu.raf.si.bank2.securities.exceptions;

public class ExternalAPILimitReachedException extends RuntimeException {

    public ExternalAPILimitReachedException() {
        super("External API limit reached! (5 calls per minute");
    }
}
