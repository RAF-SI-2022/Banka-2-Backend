package rs.edu.raf.si.bank2.Bank2Backend.requests;

import lombok.Data;

@Data
public class FutureRequestBuySell {
    Long id;
    Long userId; // na frontu je null
    String futureName; // sluzi za pretragu
    String action; // BUY - SELL
    Integer price; // ili price ili limit
    Integer limit; // ako su limit i stop OBA 0 onda se protaje-kupuje po single / odmah principu
    Integer stop;
}
