package rs.edu.raf.si.bank2.otc.cucumber.integration.balanceFailure;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.services.BalanceService;
import rs.edu.raf.si.bank2.otc.services.UserService;

public class BalanceFailureIntegrationSteps extends BalanceFailureIntegrationTestConfig {
    @Autowired
    BalanceService balanceService;

    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;

    @Given("user logs in")
    public void user_logs_in() {
        token = null;
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

    @Then("user tries to increase balance but currency not found")
    public void userTriesToIncreaseBalanceButCurrencyNotFound() {
        try {
            mockMvc.perform(post("/api/balances/increase")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "anesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "USDD",
                                    "amount": "999"
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

    @Then("user tries to increase balance but user not found")
    public void userTriesToIncreaseBalanceButUserNotFound() {
        try {
            mockMvc.perform(post("/api/balances/increase")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "aanesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "USD",
                                    "amount": "999"
                                }
                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is5xxServerError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user tries to decrease balance but some error occured")
    public void userTriesToDecreaseBalanceButSomeErrorOccured() {
        try {
            mockMvc.perform(post("/api/balances/decrease")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "anesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "USD",
                                    "amount": "9999"
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

    @Then("user tries to decrease balance but not enough money")
    public void userTriesToDecreaseBalanceButNotEnoughMoney() {
        try {
            mockMvc.perform(post("/api/balances/decrease")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "anesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "USD",
                                    "amount": "9999999999"
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

    @Then("user tries to decrease balance but balance not found")
    public void userTriesToDecreaseBalanceButBalanceNotFound() {
        try {
            mockMvc.perform(post("/api/balances/decrease")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "aaanesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "UUSD",
                                    "amount": "999"
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
}
