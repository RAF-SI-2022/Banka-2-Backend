package com.raf.si.Banka2Backend.requests;

import lombok.Data;

@Data
public class FutureRequestBuySell {
    String stockName;
    Integer amountToBuy;
    String action; //BUT / SELL
    Integer limit;
    Integer stop;
    Boolean allOrNone;
    Boolean margin;

}
