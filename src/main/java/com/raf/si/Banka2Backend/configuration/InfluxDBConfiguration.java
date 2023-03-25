package com.raf.si.Banka2Backend.configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfiguration {

  @Bean(destroyMethod = "close")
  public InfluxDBClient influxDBClient() {
    String url = "http://localhost:8086";
    String token =
        "UkrZwmfzvugMa9F7fDA6P7H6jAI3xmvFYeJ6mPhlwi9aNiHXEpHdaYsHel1aR29QsG7r_39k3zV8PYUkBTvwVA==";
    String org = "raf";
    String bucket = "raf";

    return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
  }
}
