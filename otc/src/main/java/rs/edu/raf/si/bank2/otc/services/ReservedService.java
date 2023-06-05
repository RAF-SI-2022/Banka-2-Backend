package rs.edu.raf.si.bank2.otc.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.dto.ReserveDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.utils.JwtUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ReservedService {

    private final JwtUtil jwtUtil;
    ObjectMapper mapper = new ObjectMapper();

    @Value("${services.main.host}")
    private String usersServiceHost;

    @Autowired
    public ReservedService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    public CommunicationDto sendReservation(TransactionElementDto teDto) {
        String reserveJson;
        String url = "";

        ReserveDto reserveDto = new ReserveDto(teDto.getUserId(), teDto.getMariaDbId(), teDto.getAmount());

        //todo HARD CODE NA USD
        if (teDto.getBuyOrSell() == ContractElements.BUY) {
            switch (teDto.getBalance()) {
                case CASH -> {
                    url = "/reserveMoney"; //u ovom slucaju mariaDbId je null
                    switch (teDto.getTransactionElement()) {
                        case STOCK ->reserveDto.setFutureStorage(teDto.getFutureStorageField());//direktno cenu imamo sacuvanu
                        case FUTURE -> reserveDto.setFutureStorage(teDto.getFutureStorageField().split(",")[4]);
                        case OPTION -> reserveDto.setFutureStorage(teDto.getFutureStorageField().split(",")[6]);
                    }
                }
                case MARGIN -> System.err.println("NIJE JOS DODATO"); //todo DODAJ ZA MARZNI RACUN
            }
        }
        else if (teDto.getBuyOrSell() == ContractElements.SELL) {
            switch (teDto.getTransactionElement()) {
                case STOCK -> url = "/reserveStock";
                case OPTION -> url = "/reserveOption";
                case FUTURE -> url = "/reserveFuture";
            }
        }

        try {
            reserveJson = mapper.writeValueAsString(reserveDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return sendReservePost(url, reserveJson);
    }

    public CommunicationDto sendUndoReservation(TransactionElement TElement) {
        String reserveJson;
        String url = "";
        ReserveDto reserveDto = new ReserveDto(TElement.getUserId(), TElement.getMariaDbId(), TElement.getAmount());

        //todo HARD CODE NA USD
        if (TElement.getBuyOrSell() == ContractElements.BUY) {
            switch (TElement.getBalance()) {
                case CASH -> {
                    url = "/undoReserveMoney";//u ovom slucaju mariaDbId je null
                    switch (TElement.getTransactionElement()) {
                        case STOCK -> reserveDto.setFutureStorage(TElement.getFutureStorageField());//direktno cenu imamo sacuvanu
                        case FUTURE -> reserveDto.setFutureStorage(TElement.getFutureStorageField().split(",")[4]);
                        case OPTION -> reserveDto.setFutureStorage(TElement.getFutureStorageField().split(",")[6]);
                    }
                }
                case MARGIN -> System.err.println("NIJE JOS DODATO"); //todo DODAJ ZA MARZNI RACUN
            }
        }
        else if (TElement.getBuyOrSell() == ContractElements.SELL) {
            switch (TElement.getTransactionElement()) {
                case STOCK -> url = "/undoReserveStock";
                case OPTION -> url = "/undoReserveOption";
                case FUTURE -> {
                    url = "/undoReserveFuture";
                    reserveDto.setFutureStorage(TElement.getFutureStorageField());
                }
            }
        }

        try {
            reserveJson = mapper.writeValueAsString(reserveDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return sendReservePost(url, reserveJson);
    }

    public CommunicationDto finalizeElement(TransactionElement TElement) {
        String reserveJson;
        String url = "";

        ReserveDto reserveDto = new ReserveDto(TElement.getUserId(), TElement.getMariaDbId(), TElement.getAmount());

        if (TElement.getBuyOrSell() == ContractElements.SELL) {//AKO JE SELL SAMO MI DODJA PARE
            switch (TElement.getBalance()) {
                case CASH -> {
                    url = "/undoReserveMoney"; //u ovom slucaju mariaDbId je null
                    double price = TElement.getAmount() * TElement.getPriceOfOneElement();
                    reserveDto.setFutureStorage(Double.toString(price));

                    System.err.println(reserveDto.getFutureStorage());
                }
                case MARGIN -> System.err.println("NIJE JOS DODATO"); //todo DODAJ ZA MARZNI RACUN
            }
        }
        else if (TElement.getBuyOrSell() == ContractElements.BUY) {//AKO JE BUY MORAS DA MI DODAS ELEMENTE U BAZU
            switch (TElement.getTransactionElement()) {
                case STOCK -> {
                    url = "/finalizeStock";
                    reserveDto.setFutureStorage(TElement.getFutureStorageField());
                }
                case OPTION -> {
                    url = "/finalizeOption";
                    reserveDto.setFutureStorage(TElement.getFutureStorageField());
                }
                case FUTURE -> {
                    url = "/finalizeFuture";
                    reserveDto.setFutureStorage(TElement.getFutureStorageField());
                }
            }
        }

        try {
            reserveJson = mapper.writeValueAsString(reserveDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return sendReservePost(url, reserveJson);

    }


    private CommunicationDto sendReservePost(String urlExtension, String postObjectBody) {
        System.err.println("POSALI SMO SEND RESERVE");

        String token = jwtUtil.generateToken("anesic3119rn+banka2backend+admin@raf.rs");
        String[] hostPort = usersServiceHost.split(":");
        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        String line;

        try {
            URL url = new URL("http", hostPort[0], Integer.parseInt(hostPort[1]), "/api/reserve" + urlExtension);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(postObjectBody);
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response: " + response.toString());
            connection.disconnect();
            reader.close();
            return new CommunicationDto(responseCode, response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
