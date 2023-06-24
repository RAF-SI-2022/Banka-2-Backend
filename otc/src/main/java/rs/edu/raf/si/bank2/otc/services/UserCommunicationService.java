package rs.edu.raf.si.bank2.otc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.Permission;
import rs.edu.raf.si.bank2.otc.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserCommunicationInterface;
import rs.edu.raf.si.bank2.otc.utils.JwtUtil;

@Service
public class UserCommunicationService implements UserCommunicationInterface {

    private final JwtUtil jwtUtil;
    ObjectMapper mapper = new ObjectMapper();

    @Value("${services.users.host}")
    private String usersServiceHost;

    @Value("${services.main.host}")
    private String mainServiceHost;

    @Autowired
    public UserCommunicationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean isAuthorised(PermissionName permissionName, String userEmail) {
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) return false;
        User user = null;

        CommunicationDto response = sendGet(userEmail, "/findByEmail");

        if (response.getResponseCode() == 200) {
            try {
                user = mapper.readValue(response.getResponseMsg(), User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else return false;

        // TODO los kod, ovde treba da se vrati verovatno set, pa da se odmah
        //  proveri da li postoji permission. To zahteva da permission equals
        //  bude implementiran samo po nazivu, ne i po ID-ju, jer pri
        //  inicijalizaciji permission-a ne znamo koji je ID za taj
        //  specificni permission ali znamo njegov naziv. ID polje u
        //  permissionu ne radi nista (mozda cak i smeta)
        for (Permission p : user.getPermissions()) {
            if (p.getPermissionName().equals(permissionName)
                    || p.getPermissionName().equals(PermissionName.ADMIN_USER)) return true;
        }
        return false;
    }

    @Override
    public CommunicationDto sendGet(String senderEmail, String urlExtension) {
        //        String senderEmail = args[0];
        //        String urlExtension = args[1];
        //        String service = null;
        //        if(args[2] != null){
        //            service = args[2];
        //        }
        //        System.err.println("POSALI SMO SEND GET");

        if (senderEmail == null) senderEmail = "anesic3119rn+banka2backend+admin@raf.rs";

        String token = jwtUtil.generateToken(senderEmail);
        String[] hostPort = usersServiceHost.split(":");
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        String line;

        try {
            URL url;
            url = new URL(
                    "http",
                    hostPort[0],
                    Integer.parseInt(hostPort[1]),
                    "/api/userService" + urlExtension); // zar nismo mogli
            // samo da izbacimo ovo userService iz rute i da gadjamo bilo sta anyways napravio sam ispod funkciju koja
            // to radi da ne bih zajebao
            // vec postojeci kod (kasno je ne znam vise sta se desava u kodu)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK && connection.getInputStream() != null) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else if (connection.getErrorStream() != null) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            //            System.out.println("Response Code: " + responseCode);
            //            System.out.println("Response: " + response.toString());
            connection.disconnect();
            if (reader != null) reader.close();
            return new CommunicationDto(responseCode, response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunicationDto sendGet(String senderEmail, String urlExtension, String service) {
        //        String senderEmail = args[0];
        //        String urlExtension = args[1];
        //        String service = null;
        //        if(args[2] != null){
        //            service = args[2];
        //        }
        //        System.err.println("POSALI SMO SEND GET");

        if (senderEmail == null) senderEmail = "anesic3119rn+banka2backend+admin@raf.rs";

        String token = jwtUtil.generateToken(senderEmail);
        String[] hostPort = mainServiceHost.split(":");
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        String line;

        try {
            URL url;
            url = new URL("http", hostPort[0], Integer.parseInt(hostPort[1]), "/api" + urlExtension);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK && connection.getInputStream() != null) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else if (connection.getErrorStream() != null) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response: " + response.toString());
            connection.disconnect();
            if (reader != null) reader.close();
            return new CommunicationDto(responseCode, response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommunicationDto sendPostLike(
            String urlExtension, String postObjectBody, String senderEmail, String method) {
        //        System.err.println("POSALI SMO SEND POST");

        if (senderEmail == null) senderEmail = "anesic3119rn+banka2backend+admin@raf.rs";

        String token = jwtUtil.generateToken(senderEmail);
        String[] hostPort = usersServiceHost.split(":");
        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        String line;

        try {
            URL url = new URL("http", hostPort[0], Integer.parseInt(hostPort[1]), "/api/userService" + urlExtension);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
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

    @Override
    public CommunicationDto sendDelete(String urlExtension) {
        //        System.err.println("POSALI SMO SEND DELETE");
        //
        //        String token = jwtUtil.generateToken("anesic3119rn+banka2backend+admin@raf.rs");
        //        String []hostPort = usersServiceHost.split(":");
        //        BufferedReader reader;
        //        StringBuilder response = new StringBuilder();
        //        String line;
        //
        //        try {
        //            URL url = new URL("http", hostPort[0], Integer.parseInt(hostPort[1]), "/api/userService" +
        // urlExtension);
        //            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //            connection.setRequestMethod("DELETE");
        //            connection.setRequestProperty("Authorization", "Bearer " + token);
        //            int responseCode = connection.getResponseCode();
        //
        //            if (responseCode == HttpURLConnection.HTTP_OK){
        //                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        //                while ((line = reader.readLine()) != null) {
        //                    response.append(line);
        //                }
        //            }
        //            else {
        //                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        //                while ((line = reader.readLine()) != null) {
        //                    response.append(line);
        //                }
        //            }
        //            System.out.println("Response Code: " + responseCode);
        //            System.out.println("Response: " + response.toString());
        //            connection.disconnect();
        //            reader.close();
        //            return new CommunicationDto(responseCode, response.toString());
        //        } catch (IOException e) {
        //            throw new RuntimeException(e);
        //        }
        return null;
    }
}
