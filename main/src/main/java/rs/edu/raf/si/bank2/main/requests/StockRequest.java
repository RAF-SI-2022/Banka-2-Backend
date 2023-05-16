package rs.edu.raf.si.bank2.main.requests;

import lombok.Data;

@Data
public class StockRequest {
    String stockSymbol;
    Integer amount;
    Integer limit;
    Integer stop;
    boolean allOrNone;
    boolean margin;
    Long userId; // null sa fronta, stavi se u servisima
    String currencyCode;
}
