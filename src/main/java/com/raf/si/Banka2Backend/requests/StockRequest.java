package com.raf.si.Banka2Backend.requests;

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
}
