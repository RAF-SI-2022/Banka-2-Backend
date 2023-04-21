package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Option;
import com.raf.si.Banka2Backend.repositories.mariadb.OptionRepository;
import com.raf.si.Banka2Backend.services.interfaces.OptionServiceInterface;
import com.raf.si.Banka2Backend.services.workerThreads.OptionDbWiperThread;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OptionService implements OptionServiceInterface {

    private final OptionRepository optionRepository;
    private final UserService userService;
    private final StockService stockService;
    private final OptionDbWiperThread optionDbWiperThread;

    @Autowired
    public OptionService(OptionRepository optionRepository, UserService userService, StockService stockService) {
        this.optionRepository = optionRepository;
        this.userService = userService;
        this.stockService = stockService;

        this.optionDbWiperThread = new OptionDbWiperThread(optionRepository);
//        optionDbWiperThread.start();
    }

    @Override
    public List<Option> findAll() {
        return optionRepository.findAll();
    }

    @Override
    public Option save(Option option) {
        return optionRepository.save(option);
    }


    @Override
    public Optional<Option> findById(Long id) {
        return optionRepository.findById(id);
    }

    @Override
    public List<Option> findByUserId(Long userId) {
        return optionRepository.findAllByUserId(userId);
    }

    @Override
    public List<Option> findByStock(String stockSymbol) {
        List<Option> requestedOptions = optionRepository.findAllByStockSymbol("AAPL");
        if (requestedOptions.isEmpty()) {
            optionRepository.saveAll(getFromExternalApi(stockSymbol, ""));
        }
        return optionRepository.findAllByStockSymbol(stockSymbol.toUpperCase());
    }

    @Override
    public List<Option> findByStockAndDate(String stockSymbol, String regularDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(regularDate, formatter);

        List<Option> requestedOptions = optionRepository.findAllByStockSymbolAndExpirationDate(stockSymbol.toUpperCase(), date);
        if (requestedOptions.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateMils = null;
            try {
                dateMils = dateFormat.parse(regularDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String parsedDate = "" + dateMils.getTime() / 10000;
//            optionRepository.saveAll(getFromExternalApi(stockSymbol, parsedDate));
            System.out.println(getFromExternalApi(stockSymbol, parsedDate));
        }
        return optionRepository.findAllByStockSymbolAndExpirationDate(stockSymbol.toUpperCase(), date);
    }


    public List<Option> getFromExternalApi(String stockSymbol, String date) {

        String apiUrl;
        List<Option> optionList = new ArrayList<>();

        if (date == null)
            apiUrl = "https://query1.finance.yahoo.com/v7/finance/options/aapl";
        else
            apiUrl = "https://query1.finance.yahoo.com/v7/finance/options/" + stockSymbol + "?date=" + date;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        HttpResponse<String> response;


        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject fullResponse = new JSONObject(response.body());

            JSONObject optionChain = fullResponse.getJSONObject("optionChain");
            JSONArray result = optionChain.getJSONArray("result");

            JSONObject object = result.getJSONObject(0);

            String underlyingSymbol = object.getString("underlyingSymbol");

            JSONArray optionsArray = object.getJSONArray("options");
            JSONObject options = optionsArray.getJSONObject(0);

            JSONArray callsArray = options.getJSONArray("calls");

            for (Object o : callsArray) {

                JSONObject json = (JSONObject) o;

                Integer contractSize = 100;
                Double price = json.getDouble("lastPrice");

                Double maintenanceMargin = contractSize * 0.5 * price;

                Option newOption = Option.builder()
                        .contractSymbol(json.getString("contractSymbol"))
                        .stockSymbol(stockSymbol.toUpperCase())
                        .optionType("CALL")
                        .strike(json.getDouble("strike"))
                        .impliedVolatility(json.getDouble("impliedVolatility"))
                        .expirationDate(Instant.ofEpochMilli(json.getInt("expiration") * 1000L).atZone(ZoneId.systemDefault()).toLocalDate())
                        .openInterest(json.getInt("openInterest"))
                        .contractSize(contractSize)
                        .price(price)
                        .maintenanceMargin(maintenanceMargin)
                        .build();

                optionList.add(newOption);
            }

            JSONArray putsArray = options.getJSONArray("puts");

            for (Object o : callsArray) {

                JSONObject json = (JSONObject) o;

                Integer contractSize = 100;
                Double price = json.getDouble("lastPrice");

                Double maintenanceMargin = contractSize * 0.5 * price;

                Option newOption = Option.builder()
                        .contractSymbol(json.getString("contractSymbol"))
                        .stockSymbol(stockSymbol.toUpperCase())
                        .optionType("PUT")
                        .strike(json.getDouble("strike"))
                        .impliedVolatility(json.getDouble("impliedVolatility"))
                        .expirationDate(Instant.ofEpochMilli(json.getInt("expiration") * 1000L).atZone(ZoneId.systemDefault()).toLocalDate())
                        .openInterest(json.getInt("openInterest"))
                        .contractSize(contractSize)
                        .price(price)
                        .maintenanceMargin(maintenanceMargin)
                        .build();

                optionList.add(newOption);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.err.println(optionList.size());

        return optionList;
    }

}