package rs.edu.raf.si.bank2.main.requests;

import lombok.Data;

import java.io.Serializable;

@Data
public class FutureRequestBuySell implements Serializable {
    Long id;
    Long userId; // na frontu je null
    String futureName; // sluzi za pretragu
    String action; // BUY - SELL
    Integer price; // ili price ili limit
    String currencyCode;
    Integer limit; // ako su limit i stop OBA 0 onda se protaje-kupuje po single / odmah principu
    Integer stop;
}
