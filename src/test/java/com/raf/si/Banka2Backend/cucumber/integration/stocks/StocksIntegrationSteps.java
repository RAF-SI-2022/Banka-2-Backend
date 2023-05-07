package com.raf.si.Banka2Backend.cucumber.integration.stocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.models.mariadb.Exchange;
import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.services.StockService;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class StocksIntegrationSteps extends StocksIntegrationTestConfig {

    @Autowired
    private StockService stockService;

    @Autowired
    protected MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Stock testStock;
    private static String token;

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

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
        } catch (Exception e) {
            fail("User token null or empty - not logged in properly");
        }
    }

    @Then("user gets all stocks from database")
    public void userGetsAllStocksFromDatabase() {

        try {
            mockMvc.perform(get("/api/stock")
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

    @And("there is a stock in database")
    public void thereIsAStockInDatabase() {
        testStock = Stock.builder()
                .id(1L)
                .exchange(new Exchange())
                .symbol("AAPL")
                .companyName("Apple Inc")
                .dividendYield(new BigDecimal("0.005800"))
                .outstandingShares(Long.parseLong("15821900000"))
                .openValue(new BigDecimal("161.53000"))
                .highValue(new BigDecimal("162.47000"))
                .lowValue(new BigDecimal("161.27000"))
                .priceValue(new BigDecimal("162.36000"))
                .volumeValue(Long.parseLong("49443818"))
                .lastUpdated(LocalDate.parse("2023-04-03"))
                .previousClose(new BigDecimal("160.77000"))
                .changeValue(new BigDecimal("1.59000"))
                .changePercent("0.9890%")
                .websiteUrl("https://www.apple.com")
                .build();
    }

    @Then("user gets stock by id from database")
    public void userGetsStockByIdFromDatabase() throws IOException, JSONException {

        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(get("/api/stock/" + testStock.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }

        JSONObject actualStockJson = new JSONObject(mvcResult.getResponse().getContentAsString());

        assertNotNull(actualStockJson, "Json is not null");
    }

    @Then("user gets stock by symbol from database")
    public void userGetsStockBySymbolFromDatabase() throws UnsupportedEncodingException, JSONException {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(get("/api/stock/symbol/" + testStock.getSymbol())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }

        JSONObject actualStockJson = new JSONObject(mvcResult.getResponse().getContentAsString());

        assertNotNull(actualStockJson, "Json is not null");
    }
}
