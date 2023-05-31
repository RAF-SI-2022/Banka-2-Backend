package rs.edu.raf.si.bank2.otc.cucumber.integration.transactions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.services.TransactionService;
import rs.edu.raf.si.bank2.otc.services.UserService;

public class TransactionIntegrationSteps extends TransactionIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Optional<User> loggedInUser;
    protected static Optional<User> testUser;

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

    @Then("user gets all orders")
    public void user_gets_all_orders() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/transactions")
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

    @Then("user gets transactions by TransactionsByCurrencyValue")
    public void user_gets_transactions_by_transactions_by_currency_value() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/transactions/USD")
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
}
