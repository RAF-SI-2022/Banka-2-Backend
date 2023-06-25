package rs.edu.raf.si.bank2.main.bootstrap.readers;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import rs.edu.raf.si.bank2.main.models.mariadb.Inflation;

public class CurrencyReader {

    private final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);
    private List<Inflation> inflations = new ArrayList<>();

    public List<CurrencyCSV> getCurrenciesFromCsv() throws FileNotFoundException {
        // TODO da li ovo treba da vrati praznu listu ili null ako neuspesno?

        String resPath = "/currencies/currencies.csv";
        Optional<String> content = rs.edu.raf.si.bank2.main.bootstrap.readers.CSVReader.getInstance()
                .readCSVString(resPath);
        if (content.isEmpty()) {
            logger.error("Failed to load CSV " + resPath);
            // TODO da li ovde treba empty list ili null?
            return new LinkedList<>();
        }

        // TODO this should be rewritten, resources cannot be accessed as
        //  "normal" files in a jar file. Hacky solution to get it working
        //  with CSVReader

        String inp = content.get();
        Reader read = new StringReader(inp);

        return new CsvToBeanBuilder<CurrencyCSV>(read)
                .withType(CurrencyCSV.class)
                .withSkipLines(1)
                .build()
                .parse();
    }

    public List<rs.edu.raf.si.bank2.main.models.mariadb.Currency> getCurrencies() throws IOException {
        List<rs.edu.raf.si.bank2.main.models.mariadb.Currency> result = new ArrayList<>();
        List<CurrencyCSV> currenciesCsv = getCurrenciesFromCsv();
        for (CurrencyCSV currencyCsv : currenciesCsv) {
            rs.edu.raf.si.bank2.main.models.mariadb.Currency c = new rs.edu.raf.si.bank2.main.models.mariadb.Currency();
            c.setCurrencyName(currencyCsv.getCurrencyName());
            c.setCurrencyCode(currencyCsv.getCurrencyCode());
            c.setPolity(getCurrencyPolity(currencyCsv.getCurrencyCode()));
            c.setCurrencySymbol(getCurrencySymbol(currencyCsv.getCurrencyCode()));
            List<Inflation> inflationList = this.getInflationList(c);
            this.inflations.addAll(inflationList);
            c.setInflations(inflationList);
            result.add(c);
        }
        return result;
    }

    public List<Inflation> getInflations() {
        return this.inflations;
    }

    private String getCurrencyPolity(String currencyCode) {
        Locale locale = new Locale("", currencyCode.substring(0, 2));
        return locale.getDisplayCountry();
    }

    private String getCurrencySymbol(String currencyCode) {
        return java.util.Currency.getInstance(currencyCode).getSymbol();
    }

    private List<Inflation> getInflationList(rs.edu.raf.si.bank2.main.models.mariadb.Currency currency)
            throws IOException {
        List<Inflation> result = new ArrayList<>();

        // TODO da li ovo treba da vrati praznu listu ili null ako neuspesno?

        String resPath = "/currencies/inflations.csv";
        Optional<String> content = rs.edu.raf.si.bank2.main.bootstrap.readers.CSVReader.getInstance()
                .readCSVString(resPath);
        if (content.isEmpty()) {
            logger.error("Failed to load CSV " + resPath);
            // TODO da li ovde treba empty list ili null?
            return new LinkedList<>();
        }

        // TODO this should be rewritten, resources cannot be accessed as
        //  "normal" files in a jar file. Hacky solution to get it working
        //  with CSVReader

        String inp = content.get();
        Reader read = new StringReader(inp);

        CSVReader csvReader = new CSVReader(read);

        String[] headerRow = csvReader.readNext();
        String[] dataRow;
        int yearIndex = 2;
        String currencyCountryCode = CountryCodeMapper.getCountryCode(currency.getPolity());
        // Explanation for if: In data source inflation is tracked by
        // countries, not by currencies.
        // European Union is not a country,
        // so if currency code is EUR, we read inflation data for some
        // European country, e.g. Germany ->
        // country code is DEU.
        if (currency.getCurrencyCode().equalsIgnoreCase("EUR")) {
            currencyCountryCode = "DEU";
        }
        while ((dataRow = csvReader.readNext()) != null) {
            String countryCode = dataRow[0];
            if (!countryCode.equalsIgnoreCase(currencyCountryCode)) {
                continue;
            }
            for (int i = yearIndex; i < dataRow.length; i++) {
                if (dataRow[i] == null || dataRow[i].equals("")) {
                    continue;
                }
                float inflationRate = Float.parseFloat(dataRow[i]);
                int year = Integer.parseInt(headerRow[i]);
                Inflation inflation = new Inflation();
                inflation.setCurrency(currency);
                inflation.setInflationRate(inflationRate);
                inflation.setYear(year);
                result.add(inflation);
            }
        }
        csvReader.close();
        return result;
    }
}
