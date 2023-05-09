package rs.edu.raf.si.bank2.main.cucumber.integration.orderFailures;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.services.OrderService;
import rs.edu.raf.si.bank2.main.services.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class OrderFailuresIntegrationSteps extends OrderIntegrationFailuresTestConfig {
    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected OrderService orderService;

    protected static String token;
    protected static Optional<User> loggedInUser;
    protected static Optional<User> testUser;
    private static Order testOrder;

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
            fail("User failed to login");
        }
    }

    @When("user not logged in")
    public void user_not_logged_in() {
        token = "";
    }

    @Then("user accesses endpoint")
    public void user_accesses_endpoint() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/orders")
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage = "JWT String argument cannot be null or empty.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user accesses get all by user endpoint")
    public void user_accesses_get_all_by_user_endpoint() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/orders/0")
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage = "JWT String argument cannot be null or empty.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("there is order in non waiting status in db")
    public void there_is_order_in_non_waiting_status_in_db() {
        testOrder = this.orderService.save(new StockOrder(
                0L,
                OrderType.STOCK,
                OrderTradeType.BUY,
                OrderStatus.COMPLETE,
                "AAPL",
                2,
                1,
                "datum",
                this.userService
                        .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                        .get(),
                0,
                0,
                false,
                false,
                "usd"));
    }

    @When("user logged in")
    public void user_logged_in() {
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
            fail("User failed to login");
        }
    }

    @Then("order not approved")
    public void order_not_approved() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            patch("/api/orders/approve/" + (testOrder == null ? 0 : testOrder.getId()))
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("order not denied")
    public void order_not_denied() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            patch("/api/orders/deny/" + (testOrder == null ? 0 : testOrder.getId()))
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
