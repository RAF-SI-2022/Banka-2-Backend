package rs.edu.raf.si.bank2.main.bootstrap.readers;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContextListener;
import org.springframework.util.ResourceUtils;
import rs.edu.raf.si.bank2.main.models.mariadb.Inflation;

public class CurrencyReader {
    private List<Inflation> inflations = new ArrayList<>();

    public List<CurrencyCSV> getCurrenciesFromCsv() throws FileNotFoundException {
        // TODO da li ovo treba da vrati praznu listu ili null ako neuspesno?

        String resPath = "currencies/currencies.csv";
        URL url = ServletContextListener.class.getClassLoader().getResource(resPath);
        if (url == null) {
            System.err.println("Could not find resource: " + resPath);
            return new ArrayList<>();
        }

        return new CsvToBeanBuilder<CurrencyCSV>(new FileReader(ResourceUtils.getFile(url.getPath())))
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

        String resPath = "currencies/inflations.csv";
        URL url = ServletContextListener.class.getClassLoader().getResource(resPath);
        if (url == null) {
            System.err.println("Could not find resource: " + resPath);
            return result;
        }

        CSVReader csvReader;
        try {
            csvReader = new CSVReader(new FileReader(url.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        }

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
