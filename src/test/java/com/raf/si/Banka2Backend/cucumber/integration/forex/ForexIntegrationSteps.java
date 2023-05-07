package com.raf.si.Banka2Backend.cucumber.integration.forex;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.dto.BuySellForexDto;
import com.raf.si.Banka2Backend.models.mariadb.Forex;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.repositories.mariadb.ForexRepository;
import com.raf.si.Banka2Backend.services.UserService;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ForexIntegrationSteps extends ForexIntegrationTestConfig {
    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Optional<User> loggedInUser;

    private static String email = "anesic3119rn+banka2backend+admin@raf.rs";
    private static String password = "admin";

    protected static Forex testForex;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ForexRepository forexRepository;

    @Given("user logs in")
    public void user_logs_in() {
        try {
            MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content(
                                    """

                                            {

                                              "email": "%s",

                                              "password": "%s"

                                            }

                                            """
                                            .formatted(email, password)))
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
            mockMvc.perform(get("/api/forex")
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
                .fromCurrencyName("United States Dollar")
                .toCurrencyName("Australian Dollar")
                .fromCurrencyCode("USD")
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
            mvcResult = mockMvc.perform(
                            get("/api/forex/" + testForex.getFromCurrencyCode() + "/" + testForex.getToCurrencyCode())
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
            actualForex = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Forex.class);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        // Perform an assertion that verifies the actual result matches the expected result
        assertEquals(testForex, actualForex);
    }

    @Then("user converts from one currency to another with db")
    public void user_converts_from_one_currency_to_another_with_db()
            throws JsonProcessingException, UnsupportedEncodingException {
        BuySellForexDto dto = new BuySellForexDto();
        dto.setFromCurrencyCode("USD");
        dto.setToCurrencyCode("AUD");
        dto.setAmount(500);
        MvcResult mvcResult = null;
        String body = new ObjectMapper().writeValueAsString(dto);
        try {
            mvcResult = mockMvc.perform(post("/api/forex/buy-sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().is(200))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("there is no forex record in database")
    public void there_is_no_forex_record_in_database() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String matrixTime = dateFormat.format(new Date(1999, 5, 31, 0, 0));
        testForex = Forex.builder()
                .id(1L)
                .fromCurrencyName("United States Dollar")
                .toCurrencyName("Danish Krone")
                .fromCurrencyCode("USD")
                .toCurrencyCode("DKK")
                .bidPrice("1000")
                .askPrice("500")
                .exchangeRate("2")
                .timeZone("UTC")
                .lastRefreshed(matrixTime)
                .build();
    }

    @Then("user gets forex by currency to and currency from from api")
    public void user_gets_forex_by_currency_to_and_currency_from_from_api() {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(
                            get("/api/forex/" + testForex.getFromCurrencyCode() + "/" + testForex.getToCurrencyCode())
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        //        Forex actualForex = null;
        //        try {
        //            actualForex = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Forex.class);
        //        } catch (IOException e) {
        //            fail(e.getMessage());
        //        }
        //
        //        // Perform an assertion that verifies the actual result matches the expected result
        //        assertEquals(testForex.getFromCurrencyName(), actualForex.getFromCurrencyName());
        //        assertEquals(testForex.getToCurrencyCode(), actualForex.getToCurrencyCode());
        //        assertNotEquals(testForex.getLastRefreshed(), actualForex.getLastRefreshed());
    }

    @Then("user converts from one currency to another with api")
    public void user_converts_from_one_currency_to_another_with_api()
            throws JsonProcessingException, UnsupportedEncodingException {
        BuySellForexDto dto = new BuySellForexDto();
        dto.setFromCurrencyCode("USD");
        dto.setToCurrencyCode("DKK");
        dto.setAmount(112);
        MvcResult mvcResult = null;
        String body = new ObjectMapper().writeValueAsString(dto);
        try {
            mvcResult = mockMvc.perform(post("/api/forex/buy-sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Forex actualForex = null;
        try {
            actualForex = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Forex.class);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertEquals(testForex.getFromCurrencyName(), actualForex.getFromCurrencyName());
        assertEquals(testForex.getToCurrencyCode(), actualForex.getToCurrencyCode());
        assertNotEquals(testForex.getLastRefreshed(), actualForex.getLastRefreshed());
    }
}
