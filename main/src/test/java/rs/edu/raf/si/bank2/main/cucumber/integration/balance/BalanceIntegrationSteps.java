package rs.edu.raf.si.bank2.main.cucumber.integration.balance;

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
import rs.edu.raf.si.bank2.main.models.mariadb.Balance;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.BalanceService;
import rs.edu.raf.si.bank2.main.services.UserService;

public class BalanceIntegrationSteps extends BalanceIntegrationTestConfig {

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

    @Then("user user gets all balances from database by user id")
    public void userUserGetsAllBalancesFromDatabaseByUserId() {
        Optional<User> user = this.userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        Long id = user.get().getId();
        try {
            mockMvc.perform(get("/api/balances/" + id)
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

    @Then("user increases balance")
    public void userIncreasesBalance() {
        Float oldAmount = this.getBalanceAmount("anesic3119rn+banka2backend+admin@raf.rs", "USD");
        try {
            mockMvc.perform(post("/api/balances/increase")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "anesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "USD",
                                    "amount": "999"
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
        Float newAmount = this.getBalanceAmount("anesic3119rn+banka2backend+admin@raf.rs", "USD");
        assertEquals(Math.round(oldAmount + 999f), Math.round(newAmount));
    }

    @Then("user decreases balance")
    public void userDecreasesBalance() {
        Float oldAmount = this.getBalanceAmount("anesic3119rn+banka2backend+admin@raf.rs", "USD");
        this.balanceService.reserveAmount(999f, "anesic3119rn+banka2backend+admin@raf.rs", "USD", false);
        try {
            mockMvc.perform(post("/api/balances/decrease")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "userEmail": "anesic3119rn+banka2backend+admin@raf.rs",
                                    "currencyCode": "USD",
                                    "amount": "999"
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
        Float newAmount = this.getBalanceAmount("anesic3119rn+banka2backend+admin@raf.rs", "USD");
        assertEquals(Math.round(oldAmount - 999f), Math.round(newAmount));
    }

    private Float getBalanceAmount(String userEmail, String currencyCode) {
        Balance balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(userEmail, currencyCode);
        return balance.getAmount();
    }
}
