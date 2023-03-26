package com.raf.si.Banka2Backend.configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfiguration {

  @Value("${spring.influx.url}")
  private String url;

  @Value("${spring.influx.user")
  private String username;

  @Value("${spring.influx.password}")
  private String password;

  @Bean(destroyMethod = "close")
  public InfluxDBClient influxDBClient() {
    return InfluxDBClientFactory.create(url, this.username, this.password.toCharArray());
  }
}
