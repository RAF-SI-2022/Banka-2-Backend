package com.raf.si.Banka2Backend.cucumber.integration.forexFailure;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class ForexFailureIntegrationSteps extends ForexFailureIntegrationTestConfig {
    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Optional<User> loggedInUser;
    private static String email = "anesic3119rn+banka2backend+admin@raf.rs";
    private static String password = "admin";

    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static String systemTime = dateFormat.format(new Date());
    protected static Forex testForex = Forex.builder()
            .id(1L)
            .fromCurrencyName("ZZZSS")
            .toCurrencyName("ZZZSS")
            .fromCurrencyCode("SSSHHH#@")
            .toCurrencyCode("SSSSDSD#@")
            .bidPrice("5")
            .askPrice("5")
            .exchangeRate("AAA")
            .timeZone("UTC")
            .lastRefreshed(systemTime)
            .build();

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

    @Then("user cant find forex in database or by api")
    public void user_cant_find_forex_in_database_or_by_api() {
        BuySellForexDto dto = new BuySellForexDto();
        dto.setToCurrencyCode("ZZZFF");
        dto.setFromCurrencyCode("SSSFF");
        //        dto.setAmountOfMoney(500);
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(
                            get("/api/forex/" + testForex.getFromCurrencyCode() + "/" + testForex.getToCurrencyCode())
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().is(404))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user doesn't have balance in currency he requested to convert from")
    public void user_doesn_t_have_balance_in_currency_he_requested_to_convert_from()
            throws UnsupportedEncodingException, JsonProcessingException {
        BuySellForexDto dto = new BuySellForexDto();
        dto.setFromCurrencyCode("SEK");
        dto.setToCurrencyCode("JPY");
        //        dto.setAmountOfMoney(500);
        MvcResult mvcResult = null;
        String body = new ObjectMapper().writeValueAsString(dto);
        try {
            mvcResult = mockMvc.perform(post("/api/forex/buy-sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().is(400))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertEquals("User with email " + email + ", doesn't have balance in currency Swedish Krona", errorMsg);
    }

    @Then("user doesn't have enough balance in currency he requested to convert from")
    public void user_doesn_t_have_enough_balance_in_currency_he_requested_to_convert_from()
            throws JsonProcessingException, UnsupportedEncodingException {
        BuySellForexDto dto = new BuySellForexDto();
        dto.setFromCurrencyCode("RSD");
        dto.setToCurrencyCode("USD");
        //        dto.setAmountOfMoney(10000000);
        MvcResult mvcResult = null;
        String body = new ObjectMapper().writeValueAsString(dto);
        try {
            mvcResult = mockMvc.perform(post("/api/forex/buy-sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().is(400))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        String errorMsg = mvcResult.getResponse().getContentAsString();
        System.out.println(errorMsg);
        assertEquals(
                "User with email "
                        + email
                        + ", doesn't have enough money in currency Serbian Dinar for buying 10000000 USD(United States Dollar)",
                errorMsg);
    }
}
