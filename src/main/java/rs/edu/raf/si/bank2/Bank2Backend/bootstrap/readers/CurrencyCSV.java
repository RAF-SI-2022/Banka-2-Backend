package rs.edu.raf.si.bank2.Bank2Backend.bootstrap.readers;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class CurrencyCSV {
    @CsvBindByPosition(position = 0)
    private String currencyCode;

    @CsvBindByPosition(position = 1)
    private String currencyName;

    @Override
    public String toString() {
        return "CurrencyCSV{" + "currencyCode='" + currencyCode + '\'' + ", currencyName='" + currencyName + '\'' + '}';
    }
}
