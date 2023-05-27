package rs.edu.raf.si.bank2.main.services;

import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.services.interfaces.CommunicationInterface;
import rs.edu.raf.si.bank2.main.utils.JwtUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class CommunicationService implements CommunicationInterface {

    private final JwtUtil jwtUtil = new JwtUtil();


    @Override
    public String testComs() throws IOException, InterruptedException {

        String host = "127.0.0.1";
        int port = 8081;

        URL url = new URL("http", host, port, "/api/userService/testMethod");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        System.out.println("Response Code: " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        System.out.println("Response: " + response.toString());
        connection.disconnect();

        return response.toString();
    }

    @Override
    public String isAuthorised(PermissionName permissionName, String userEmail) {

        String host = "127.0.0.1";
        int port = 8081;

        URL url = null;
        try {
            url = new URL("http", host, port, "/api/userService/isAuth/" + permissionName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + jwtUtil.generateToken(userEmail));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            return response.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
