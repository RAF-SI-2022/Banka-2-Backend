package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.bootstrap.BootstrapData;
import com.raf.si.Banka2Backend.models.mariadb.Forex;
import com.raf.si.Banka2Backend.repositories.mariadb.ForexRepository;
import com.raf.si.Banka2Backend.services.interfaces.ForexServiceInterface;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForexService implements ForexServiceInterface {

  private final ForexRepository forexRepository;

  @Autowired
  public ForexService(ForexRepository forexRepository) {
    this.forexRepository = forexRepository;
  }

  @Override
  public List<Forex> findAll() {
    return forexRepository.findAll();
  }

  @Override
  public Forex getForexForCurrencies(String fromCurrency, String toCurrency) {

    Optional<Forex> forex =
        forexRepository.findForexByFromCurrencyCodeAndToCurrencyCode(fromCurrency, toCurrency);
    if (forex.isPresent()) {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String systemTime = dateFormat.format(new Date());

      try {
        Date lastModifiedDate =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(forex.get().getLastRefreshed());
        Date currentDateTime = dateFormat.parse(systemTime);
        long timeDiff =
            currentDateTime.getTime()
                - (lastModifiedDate.getTime() + 7200000L); // vucemo sa api-a koji je 2h iza
        // todo nadaj se da nema neka druga zona
        if (timeDiff >= 1 * 60 * 1000) { // 30 minutes have passed since last modified date
          Forex forexFromApi = getForexFromApi(fromCurrency, toCurrency);
          if (forexFromApi != null) {
            forex.get().setExchangeRate(forexFromApi.getExchangeRate());
            forex.get().setLastRefreshed(forexFromApi.getLastRefreshed());
            forex.get().setBidPrice(forexFromApi.getBidPrice());
            forex.get().setAskPrice(forexFromApi.getAskPrice());
            forexRepository.save(forex.get());

            return forexFromApi;
          }
        } else return forex.get(); // Less than 30 minutes have passed since last modified date
      } catch (ParseException e) {
        e.printStackTrace();
        return forex
            .get(); // sigurnosti get - ako puknte try catch (ako pukne api) (ima ga u bazi satri)
      }
    }

    Forex forexFromApi = getForexFromApi(fromCurrency, toCurrency);

    if (forexFromApi != null) {
      forexRepository.save(forexFromApi);
      return forexFromApi;
    }

    return null;
  }

  private Forex getForexFromApi(String fromCurrency, String toCurrency) {
    try {
      String apiUrl =
          "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency="
              + fromCurrency
              + "&to_currency="
              + toCurrency
              + "&apikey="
              + BootstrapData.forexApiKey;

      URL url = new URL(apiUrl);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();

      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JSONObject jsonResponse = new JSONObject(response.toString());
      JSONObject exchangeRateObject = jsonResponse.getJSONObject("Realtime Currency Exchange Rate");

      Forex forex = new Forex();

      forex.setFromCurrencyCode(exchangeRateObject.getString("1. From_Currency Code"));
      forex.setFromCurrencyName(exchangeRateObject.getString("2. From_Currency Name"));
      forex.setToCurrencyCode(exchangeRateObject.getString("3. To_Currency Code"));
      forex.setToCurrencyName(exchangeRateObject.getString("4. To_Currency Name"));
      forex.setExchangeRate(exchangeRateObject.getString("5. Exchange Rate"));
      forex.setLastRefreshed(exchangeRateObject.getString("6. Last Refreshed"));
      forex.setTimeZone(exchangeRateObject.getString("7. Time Zone"));
      forex.setBidPrice(exchangeRateObject.getString("8. Bid Price"));
      forex.setAskPrice(exchangeRateObject.getString("9. Ask Price"));

      return forex;
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
    return null;
  }
}
