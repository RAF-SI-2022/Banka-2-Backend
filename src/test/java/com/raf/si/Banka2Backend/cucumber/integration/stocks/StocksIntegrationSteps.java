package com.raf.si.Banka2Backend.cucumber.integration.stocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.models.mariadb.Exchange;
import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import com.raf.si.Banka2Backend.services.StockService;
import com.raf.si.Banka2Backend.services.UserStockService;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class StocksIntegrationSteps extends StocksIntegrationTestConfig {

    @Autowired
    private StockService stockService;

    @Autowired
    private UserStockService userStockService;

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

    @Then("user gets his user stocks")
    public void user_gets_his_user_stocks() {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(get("/api/stock/user-stocks")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotNull(mvcResult);
    }

    @Then("user  buys stock")
    public void user_buys_stock() {
        try {
            mockMvc.perform(post("/api/stock/buy")
                            .contentType("application/json")
                            .content(
                                    """
                                            {
                                            "stockSymbol": "AAPL",
                                            "amount":"10",
                                            "limit":"0",
                                            "stop":"0",
                                            "allOrNone":false,
                                            "margin":false,
                                            "userId":"1",
                                            "currencyCode":"USD"
                                            }
                                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    //    @Then("user gets stock history")
    //    public void user_gets_stock_history() {
    //        try {
    //            MvcResult mvcResult = mockMvc.perform(get("/api/stock/1/history/ONE_DAY")
    //                            .contentType("application/json")
    //                            .header("Content-Type", "application/json")
    //                            .header("Access-Control-Allow-Origin", "*")
    //                            .header("Authorization", "Bearer " + token))
    //                    .andExpect(status().isOk())
    //                    .andReturn();
    //            assertNotNull(mvcResult);
    //        } catch (Exception e) {
    //            fail(e.getMessage());
    //        }
    //    }
    //

    @Then("user sells stock")
    public void user_sells_stock() {
        try {
            Optional<UserStock> userStockTest = userStockService.findUserStockByUserIdAndStockSymbol(1, "AAPL");
            if (userStockTest.get().getAmount() < 10) {
                userStockTest.get().setAmount(100);
                userStockService.save(userStockTest.get());
                System.out.println("Dodato extra");
            }

            mockMvc.perform(post("/api/stock/sell")
                            .contentType("application/json")
                            .content(
                                    """
                                            {
                                            "stockSymbol": "AAPL",
                                            "amount":"10",
                                            "limit":"0",
                                            "stop":"0",
                                            "allOrNone":false,
                                            "margin":false,
                                            "userId":"1",
                                            "currencyCode":"USD"
                                            }
                                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user removes stock")
    public void user_removes_stock() {
        try {
            mockMvc.perform(post("/api/stock/remove/AAPL")
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
