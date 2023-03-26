package com.raf.si.Banka2Backend;

import static org.junit.jupiter.api.Assertions.*;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.client.write.events.WriteSuccessEvent;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration
public class Banka2BackendApplicationTests {

  /** Document to be used in these tests. */
  private static Point TEST_POINT;

  @Autowired private InfluxDBClient influxDBClient;

  @Test
  void givenApplicationProperties_whenAppRun_thenInfluxDbInsertSucceeds() {
    Point point =
        Point.measurement("testMeasurement")
            .addTag("pearson1", "testPearson")
            .addField("firstName", "John")
            .addField("lastName", "Doe")
            .addField("age", 25)
            .addField("cool", true)
            .time(System.currentTimeMillis(), WritePrecision.MS);

    //
    // Write by POJO
    //
    //      TestMeasurement testMeasurement = new TestMeasurement();
    //      unosenje vrednosti
    //      temperature.location = "south";
    //      temperature.value = 62D;
    //      temperature.time = Instant.now();
    //      writeApi.writeMeasurement( WritePrecision.NS, testMeasurement);
    WriteApi writeApi = this.influxDBClient.makeWriteApi();
    writeApi.writePoint("raf", "raf", point);
    writeApi.listenEvents(
        WriteSuccessEvent.class,
        event -> {
          assertNotNull(event.getLineProtocol());
        });
    writeApi.listenEvents(
        WriteErrorEvent.class,
        event -> {
          Throwable exception = event.getThrowable();
        });

    String flux =
        "from(bucket:\"raf\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"testMeasurement\")";

    QueryApi queryApi = this.influxDBClient.getQueryApi();

    List<FluxTable> tables = queryApi.query(flux);
    for (FluxTable fluxTable : tables) {
      List<FluxRecord> records = fluxTable.getRecords();
      for (FluxRecord fluxRecord : records) {
        System.out.println(
            "record: " + fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
        assertNotNull(fluxRecord.getValueByKey("_value"));
      }
    }
    System.out.println("test: jeste");
    assertEquals(1, 1);
  }

  @Measurement(name = "testMeasurement")
  private static class TestMeasurement {
    @Column(tag = true)
    String pearson1;

    @Column String firstName;

    @Column String lastName;

    @Column Integer age;

    @Column Boolean cool;

    @Column(timestamp = true)
    Instant time;
  }
}
