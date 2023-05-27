package rs.edu.raf.si.bank2.main.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.PropertySource;
import rs.edu.raf.si.bank2.main.services.interfaces.CommunicationInterface;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;


@Service
//@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "main/src/main/resources")
public class CommunicationService implements CommunicationInterface {

//    @Autowired
//    private Environment env;

//    @Value(value = "test")
//    String host;
//
//    @Value("${test}")
//    private String test;


//    Properties properties = new Properties();

//    public CommunicationService() {
//        try {
//            System.out.println("KURACCC");

//            System.out.println(host);
//            System.out.println(test);
//            System.out.println(env.getProperty("test"));

//            properties.load(new FileInputStream("application.properties"));
//            System.out.println(properties.getProperty("spring.datasource.username"));
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public String testComs() throws IOException, InterruptedException {

        String host = "127.0.0.1";
        int port = 8081;

        URL url = new URL("http", host, port, "/api/userService/testMethod");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbmVzaWMzMTE5cm4rYmFua2EyYmFja2VuZCthZG1pbkByYWYucnMiLCJleHAiOjE2ODUyMjg5MjQsImlhdCI6MTY4NTE5MjkyNH0.xZEVFAQVAfrLF97ryuCXhn0YR_MOC3JaUi1xFOwZ0-RrZ3EiDPaQW9F2cAn6DTx_a1ta-xOvraN7s3sfiggddw");

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


}
