package rs.edu.raf.si.bank2.main.cucumber.integration.stockFailure;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.models.mariadb.Stock;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.StockService;
import rs.edu.raf.si.bank2.main.services.UserService;

public class StockFailureIntegrationSteps extends StockFailureIntegrationTestConfig {

    @Autowired
    private StockService stockService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected static Optional<User> loggedInUser;
    private static Stock testStock;
    private static String token;

    @Given("user logged in")
    public void user_logged_in() {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");

        try {
            assertNotNull(user);
            assertEquals("anesic3119rn+banka2backend+admin@raf.rs", user.get().getEmail());
            loggedInUser = user;

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
            fail(e.getMessage());
        }
    }

    @When("stock doesnt exist")
    public void stock_doesnt_exist() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                Stock stock = stockService.getStockById(-1L);
                assertNull(stock);
            });
            String expectedMessage = "Stock with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets nonexistent stock by id")
    public void user_gets_nonexistent_stock_by_id() {
        try {
            mockMvc.perform(get("/api/stock/" + -1L)
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @When("stock with symbol doesnt exist")
    public void stock_with_symbol_doesnt_exist() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                Stock stock = stockService.getStockBySymbol("ASDF");
                assertNull(stock);
            });
            String expectedMessage = "Stock with symbol <ASDF> not found.";
            String actualMessage = exception.getMessage();
//            assertEquals(expectedMessage, actualMessage);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets nonexistent stock by symbol")
    public void user_gets_nonexistent_stock_by_symbol() {
        try {
            mockMvc.perform(get("/api/stock/symbol/ASDF")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("user without permissions logs in")
    public void user_without_permissions_logs_in() {
        try {

            Optional<User> nonuser = userService.findByEmail("nonpriv@gmail.com");
            if (nonuser.isEmpty()) {
                // pravimo test usera
                mockMvc.perform(post("/api/users/register")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "firstName": "nonPrivUser",
                                                  "lastName": "nonPrivUserLn",
                                                  "email": "nonpriv@gmail.com",
                                                  "password": "1234",
                                                  "permissions": [],
                                                  "jobPosition": "TEST",
                                                  "active": true,
                                                  "jmbg": "1231231231235",
                                                  "phone": "640601548865",
                                                  "dailyLimit": 5000
                                                }
                                                """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            }

            // logujemo se kao test user
            MvcResult mvcResult = mockMvc.perform(
                            post("/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "nonpriv@gmail.com",
                                                      "password": "1234"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

            Optional<User> user = userService.findByEmail("nonpriv@gmail.com");
            assertNotNull(user);
            assertEquals("nonpriv@gmail.com", user.get().getEmail());
            loggedInUser = user;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @And("doesnt have readUser perms")
    public void doesnt_have_readUser_perms() {
        try {
            assertFalse(loggedInUser.get().getPermissions().contains(PermissionName.READ_USERS));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @When("user doesnt have perms")
    public void user_doesnt_have_perms() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
            assertEquals(loggedInUser.get().getPermissions().size(), 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user tries to buy stock")
    public void user_tries_to_buy_stock() {
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
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets nonexistent stock history")
    public void user_gets_nonexistent_stock_history() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/stock/-1/history/ONE_DAY")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
            assertNotNull(mvcResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user tries to sell stock")
    public void user_tries_to_sell_stock() {
        try {
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
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user tries to get stock")
    public void user_tries_to_get_stock() {
        try {

            mockMvc.perform(get("/api/stock/user-stocks")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user tries to remove stock")
    public void user_tries_to_remove_stock() {
        try {
            mockMvc.perform(post("/api/stock/remove/AAPL")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
