package com.raf.si.Banka2Backend.cucumber.integration.currencies;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.services.CurrencyService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class CurrenciesIntegrationSteps extends CurrenciesIntegrationTestConfig {
  @Autowired private CurrencyService currencyService;

  @Autowired protected MockMvc mockMvc;
  protected static String token;

  protected static Currency testCurrency =
      Currency.builder()
          .id(1L)
          .currencyName("Euro")
          .currencyCode("EUR")
          .currencySymbol("€")
          .polity("European Union")
          .inflations(null)
          .build();

  @Given("user logs in")
  public void user_logs_in() {
    token = null;
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
      fail("Test user failed to login");
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

  @Then("user gets all currencies from database")
  public void user_gets_currencies() {
    try {
      mockMvc
          .perform(
              get("/api/currencies/")
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

  // Test user gets currency by id
  @Then("user gets currency by id from database")
  public void user_gets_currency_by_id() {
    try {
      mockMvc
          .perform(
              get("/api/currencies/" + testCurrency.getId())
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

  @Then("user gets currency by currency code from database")
  public void user_gets_currency_by_code() {
    try {
      mockMvc
          .perform(
              get("/api/currencies/code/" + testCurrency.getCurrencyCode())
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
}
