 package com.raf.si.Banka2Backend.cucumber.integration.forex;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 import com.jayway.jsonpath.JsonPath;
 import com.raf.si.Banka2Backend.models.mariadb.Forex;
 import com.raf.si.Banka2Backend.models.mariadb.User;
 import com.raf.si.Banka2Backend.repositories.mariadb.ForexRepository;
 import com.raf.si.Banka2Backend.services.UserService;
 import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
 import io.cucumber.java.en.Given;
 import io.cucumber.java.en.Then;
 import io.cucumber.java.en.When;

 import java.io.IOException;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Optional;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.test.web.servlet.MockMvc;
 import org.springframework.test.web.servlet.MvcResult;

 public class ForexIntegrationSteps extends ForexIntegrationTestConfig {
  @Autowired private UserService userService;
  @Autowired protected MockMvc mockMvc;
  protected static String token;
  protected static Optional<User> loggedInUser;

  protected static Forex testForex;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  ForexRepository forexRepository;

  @Given("user logs in")
  public void user_logs_in() {
    try {
      MvcResult mvcResult =
          mockMvc
              .perform(
                  post("/auth/login")
                      .contentType("application/json")
                      .content(
                          """

        {

          "email": "anesic3119rn+banka2backend+admin@raf.rs",

          "password": "admin"

        }

        """))
              .andExpect(status().isOk())
              .andReturn();
      token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
    } catch (Exception e) {
      fail("User failed to login");
    }
  }

  @When("user is logged in")
  public void user_is_logged_in() {
    try {
      assertNotEquals(token, null);
      assertNotEquals(token, "");
    } catch (Exception e) {
      fail("User token null or empty - not logged in properly");
    }
  }

  @Then("user gets all forex from database")
  public void user_gets_all_forex_from_database() {
    try {
      mockMvc
              .perform(
                      get("/api/forex")
                              .contentType("application/json")
                              .header("Content-Type", "application/json")
                              .header("Access-Control-Allow-Origin", "*")
                              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Given("there is a forex record in database")
  public void there_is_a_forex_record_in_database() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String systemTime = dateFormat.format(new Date());
    testForex = Forex.builder()
            .id(1L)
            .fromCurrencyName("Argentine Peso")
            .toCurrencyName("Australian Dollar")
            .fromCurrencyCode("ARS")
            .toCurrencyCode("AUD")
            .bidPrice("30")
            .askPrice("50")
            .exchangeRate("1.85")
            .timeZone("UTC")
            .lastRefreshed(systemTime)
            .build();
    forexRepository.save(testForex);
  }

  @Then("user gets forex by currency to and currency from from database")
  public void user_gets_forex_by_currency_to_and_currency_from_from_database() {
      MvcResult mvcResult = null;
      try {
          mvcResult =
                  mockMvc
                          .perform(
                                  get("/api/forex/" + testForex.getFromCurrencyCode()+"/"+testForex.getToCurrencyCode())
                                          .contentType("application/json")
                                          .header("Content-Type", "application/json")
                                          .header("Access-Control-Allow-Origin", "*")
                                          .header("Authorization", "Bearer " + token))
                          .andExpect(status().isOk())
                          .andReturn();
      } catch (Exception e) {
          fail(e.getMessage());
      }
      Forex actualForex = null;
      try {
          actualForex =
                  objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Forex.class);
      } catch (IOException e) {
          fail(e.getMessage());
      }
      System.out.println(testForex);
      System.out.println(actualForex);

      // Perform an assertion that verifies the actual result matches the expected result
      assertEquals(testForex, actualForex);
  }

  @Given("there is no forex record in database")
  public void there_is_no_forex_record_in_database() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String matrixTime = dateFormat.format(new Date(1999,5,31,0,0));
      testForex = Forex.builder()
              .id(1L)
              .fromCurrencyName("Australian Dollar")
              .toCurrencyName("Chinese Yuan")
              .fromCurrencyCode("AUD")
              .toCurrencyCode("CNY")
              .bidPrice("100")
              .askPrice("50000")
              .exchangeRate("2")
              .timeZone("UTC")
              .lastRefreshed(matrixTime)
              .build();
  }
     @Then("user gets forex by currency to and currency from from api")
     public void user_gets_forex_by_currency_to_and_currency_from_from_api() {
         MvcResult mvcResult = null;
         try {
             mvcResult =
                     mockMvc
                             .perform(
                                     get("/api/forex/" + testForex.getFromCurrencyCode()+"/"+testForex.getToCurrencyCode())
                                             .contentType("application/json")
                                             .header("Content-Type", "application/json")
                                             .header("Access-Control-Allow-Origin", "*")
                                             .header("Authorization", "Bearer " + token))
                             .andExpect(status().isOk())
                             .andReturn();
         } catch (Exception e) {
             fail(e.getMessage());
         }
         Forex actualForex = null;
         try {
             actualForex =
                     objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Forex.class);
         } catch (IOException e) {
             fail(e.getMessage());
         }
         System.out.println(testForex);
         System.out.println(actualForex);

         // Perform an assertion that verifies the actual result matches the expected result
         assertEquals(testForex.getFromCurrencyName(), actualForex.getFromCurrencyName());
         assertEquals(testForex.getToCurrencyCode(),actualForex.getToCurrencyCode());
         assertNotEquals(testForex.getLastRefreshed(),actualForex.getLastRefreshed());
     }
 }
