package com.raf.si.Banka2Backend.cucumber.integration.currenciesFailures;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.services.CurrencyService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class CurrenciesFailuresIntegrationSteps extends CurrenciesFailuresIntegrationTestConfig {
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    protected MockMvc mockMvc;
    protected static String token;

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

    @When("user is not logged in")
    public void user_is_not_logged_in() {
        token = null;
    }

    @Then("user can not get nonexistent currency by id")
    public void user_can_not_get_currency_by_id() {
        Long id = -1L;
        try {
            mockMvc
                    .perform(
                            get("/api/currencies/" + id)
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user can not get currency by nonexistent code from database")
    public void user_can_not_get_currency_by_code() {
        String currencyCode = "ERR";
        try {
            mockMvc
                    .perform(
                            get("/api/currencies/code/" + currencyCode)
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user can not get currencies")
    public void user_can_not_get_currencies() {
        try {
            mockMvc
                    .perform(
                            get("/api/currencies")
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*"))
                    .andExpect(status().isForbidden())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
