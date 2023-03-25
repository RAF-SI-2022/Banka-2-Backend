package com.raf.si.Banka2Backend.bootstrap.readers;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.models.mariadb.Inflation;
import java.io.*;
import java.util.*;
import org.springframework.util.ResourceUtils;

public class CurrencyReader {
  private List<Inflation> inflations = new ArrayList<>();

  public List<CurrencyCSV> getCurrenciesFromCsv() throws FileNotFoundException {
    return new CsvToBeanBuilder<CurrencyCSV>(
            new FileReader(ResourceUtils.getFile("src/main/resources/currencies/currencies.csv")))
        .withType(CurrencyCSV.class)
        .withSkipLines(1)
        .build()
        .parse();
  }

  public List<Currency> getCurrencies() throws IOException {
    List<Currency> result = new ArrayList<>();
    List<CurrencyCSV> currenciesCsv = getCurrenciesFromCsv();
    for (CurrencyCSV currencyCsv : currenciesCsv) {
      Currency c = new Currency();
      c.setCurrencyName(currencyCsv.getCurrencyName());
      c.setCurrencyCode(currencyCsv.getCurrencyCode());
      c.setPolity(getCurrencyPolity(currencyCsv.getCurrencyCode()));
      c.setCurrencySymbol(getCurrencySymbol(currencyCsv.getCurrencyCode()));
      List<Inflation> inflationList = this.getInflationList(c);
      this.inflations.addAll(inflationList);
      c.setInflationList(inflationList);
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

  private List<Inflation> getInflationList(Currency currency) throws IOException {
    List<Inflation> result = new ArrayList<>();
    String csvFilePath = "src/main/resources/currencies/inflations.csv";
    CSVReader csvReader = new CSVReader(new FileReader(csvFilePath));
    String[] headerRow = csvReader.readNext();
    String[] dataRow;
    int yearIndex = 2;
    String currencyCountryCode = CountryCodeMapper.getCountryCode(currency.getPolity());
    // Explanation for if: In data source inflation is tracked by countries, not by currencies.
    // European Union is not a country,
    // so if currency code is EUR, we read inflation data for some European country, e.g. Germany ->
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
