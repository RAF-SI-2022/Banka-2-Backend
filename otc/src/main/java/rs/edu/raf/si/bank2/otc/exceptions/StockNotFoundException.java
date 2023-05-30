package rs.edu.raf.si.bank2.otc.exceptions;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(Long id) {
        super("Stock with id <" + id + "> not found.");
    }

    public StockNotFoundException(String symbol) {
        super("Stock with symbol <" + symbol + "> not found.");
    }
}
