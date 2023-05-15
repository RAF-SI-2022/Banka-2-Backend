package rs.edu.raf.si.bank2.main.cucumber.integration.exchanges;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.Exchange;
import rs.edu.raf.si.bank2.main.repositories.mariadb.ExchangeRepository;
import rs.edu.raf.si.bank2.main.services.ExchangeService;

public class ExchangesIntegrationSteps extends ExchangesIntegrationTestConfig {
    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    protected MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected static String token;
    protected static Exchange testExchange;

    @Autowired
    ExchangeRepository exchangeRepository;

    @Given("user logs in")
    public void user_logs_in() {
        try {
            MvcResult mvcResult = mockMvc.perform(
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

    @Given("there is an exchange record in database")
    public void there_is_an_exchange_record_in_database() {
        testExchange = new Exchange(
                2L, "Nasdaq", "NASDAQ", "XNAS", "USA", null, "America/New_York", " 09:30", " 16:00", Arrays.asList());
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


    @Then("user gets exchange by id from database")
    public void user_gets_exchange_by_id_from_database() {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(get("/api/exchange/id/" + testExchange.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Exchange actualExchange = null;
        try {
            actualExchange = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Exchange.class);
            actualExchange.setCurrency(null);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        // Perform an assertion that verifies the actual result matches the expected result
        assertEquals(testExchange, actualExchange);
    }

    @Then("user gets all exchanges from database")
    public void user_gets_all_exchanges_from_database() {
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(get("/api/exchange")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            assertNotNull(mvcResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Then("user gets exchange by acronym from database")
    public void user_gets_exchange_by_acronym_from_database() {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(get("/api/exchange/acronym/" + testExchange.getAcronym())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }
        Exchange actualExchange = null;
        try {
            actualExchange = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Exchange.class);
            actualExchange.setCurrency(null);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        // Perform an assertion that verifies the actual result matches the expected result
        assertEquals(testExchange, actualExchange);
    }

    @Then("user gets activity of exchange by MIC Code from database")
    public void user_gets_activity_of_exchange_by_mic_code_from_database() {
        try {
            mockMvc.perform(get("/api/exchange/status/" + testExchange.getMicCode())
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
