package rs.edu.raf.si.bank2.main.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.dto.CommunicationDto;
import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;
import rs.edu.raf.si.bank2.main.utils.JwtUtil;

@Service
public class UserCommunicationService implements UserCommunicationInterface {

    private final JwtUtil jwtUtil;
//
//    @Autowired
//    private UserServiceInterface userServiceInterface;

    @Value("${services.users.host}")
    private String usersServiceHost;

    @Autowired
    public UserCommunicationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

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
    public boolean isAuthorised(PermissionName permissionName, String userEmail) {
//        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) return false;
//        Optional<User> optional = userServiceInterface.findByEmail(userEmail);
//        if (optional.isEmpty()) return false;
//
//        // TODO los kod, ovde treba da se vrati verovatno set, pa da se odmah
//        //  proveri da li postoji permission. To zahteva da permission equals
//        //  bude implementiran samo po nazivu, ne i po ID-ju, jer pri
//        //  inicijalizaciji permission-a ne znamo koji je ID za taj
//        //  specificni permission ali znamo njegov naziv. ID polje u
//        //  permissionu ne radi nista (mozda cak i smeta)
//        for (Permission p : optional.get().getPermissions()) {
//            if (p.getPermissionName().equals(permissionName) || p.getPermissionName().equals(PermissionName.ADMIN_USER)) return true;
//        }
//        return false;
        return true;
    }

    @Override
    public CommunicationDto sendGet(String senderEmail, String urlExtension){
        System.err.println("POSALI SMO SEND GET");

        if (senderEmail == null) senderEmail = "anesic3119rn+banka2backend+admin@raf.rs";

        String token = jwtUtil.generateToken(senderEmail);
        String []hostPort = usersServiceHost.split(":");
        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        String line;

        try {
            URL url = new URL("http", hostPort[0], Integer.parseInt(hostPort[1]), "/api/userService" + urlExtension);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response: " + response.toString());
            connection.disconnect();
            reader.close();
            System.out.println("PRAVIMO COM DTO");
            return new CommunicationDto(responseCode, response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommunicationDto sendPostLike(String urlExtension, String method){

        return null;
    }


    @Override
    public CommunicationDto sendDelete(String urlExtension){
        System.err.println("POSALI SMO SEND DELETE");

        String token = jwtUtil.generateToken("anesic3119rn+banka2backend+admin@raf.rs");
        String []hostPort = usersServiceHost.split(":");
        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        String line;

        try {
            URL url = new URL("http", hostPort[0], Integer.parseInt(hostPort[1]), "/api/userService/" + urlExtension);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
//            System.out.println("Response Code: " + responseCode);
//            System.out.println("Response: " + response.toString());
            connection.disconnect();
            reader.close();
            return new CommunicationDto(responseCode, response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
