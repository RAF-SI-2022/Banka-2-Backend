package com.raf.si.Banka2Backend.configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.Permission;
import com.influxdb.client.domain.PermissionResource;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfiguration {

  @Value("${influxdb.url}")
  private String url;

  @Value("${influxdb.token}")
  private String token;

  @Value("${influxdb.org}")
  private String org;

  @Value("${influxdb.org}")
  private String bucket;

  @Bean(destroyMethod = "close")
  public InfluxDBClient influxDBClient() {

    return InfluxDBClientFactory.create(this.url, this.token.toCharArray(), this.org, this.bucket);
  }

  private List<Permission> generatePermissions() {
    PermissionResource readPermissionResource = new PermissionResource();
    readPermissionResource.setId("/*");

    PermissionResource writePermissionResource = new PermissionResource();
    writePermissionResource.setId("/*");

    Permission readPermission = new Permission();
    readPermission.setAction(Permission.ActionEnum.READ);
    readPermission.setResource(readPermissionResource);

    Permission writePermission = new Permission();
    writePermission.setAction(Permission.ActionEnum.WRITE);
    writePermission.setResource(writePermissionResource);
    return Arrays.asList(readPermission, writePermission);
  }
}
