package rs.edu.raf.si.bank2.main.cucumber.integration.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.services.OrderService;
import rs.edu.raf.si.bank2.main.services.UserService;

public class OrderIntegrationSteps extends OrderIntegrationTestConfig {
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

    @When("user logged in")
    public void user_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
        } catch (Exception e) {
            fail("User token null or empty - not logged in properly");
        }
    }

    @Then("user gets all orders from db")
    public void user_gets_all_orders_from_db() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/orders")
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

    @Given("there are orders in database")
    public void there_are_orders_in_database() {
        testOrder = this.orderService.save(new Order(
                0L,
                OrderType.STOCK,
                OrderTradeType.BUY,
                OrderStatus.COMPLETE,
                "testSymbol",
                2,
                1,
                "datum",
                this.userService
                        .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                        .get()));
    }

    @Then("user gets orders by user id from database")
    public void user_gets_orders_by_user_id_from_database() throws UnsupportedEncodingException {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(get("/api/orders/"
                                    + this.userService
                                            .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                                            .get()
                                            .getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertNotNull(mvcResult, "User has orders");
        this.orderService.removeOrder(testOrder);
    }

    @Given("there is order in waiting status in db")
    public void there_is_order_in_waiting_status_in_db() {
        testOrder = this.orderService.save(new StockOrder(
                0L,
                OrderType.STOCK,
                OrderTradeType.BUY,
                OrderStatus.WAITING,
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

    @Then("user Approves order")
    public void user_approves_order() {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(patch("/api/orders/approve/" + testOrder.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andReturn();

        } catch (Exception e) {

        }
        this.orderService.removeOrder(testOrder);
    }

    @Then("user Denies order")
    public void user_denies_order() {
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(patch("/api/orders/deny/" + testOrder.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }
        this.orderService.removeOrder(testOrder);
    }

    @Then("user gets value of order")
    public void userGetsValueOfOrder() {
        try {
            mockMvc.perform(get("/api/orders/value/"
                                    + this.userService
                                            .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                                            .get()
                                            .getId())
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

    @Then("user gets trade type of order")
    public void userGetsTradeTypeOfOrder() {
        try {
            mockMvc.perform(get("/api/orders/tradeType/"
                                    + this.userService
                                            .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                                            .get()
                                            .getId())
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

    @Then("user gets type of order")
    public void userGetsTypeOfOrder() {
        try {
            mockMvc.perform(get("/api/orders/orderType/"
                                    + this.userService
                                            .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                                            .get()
                                            .getId())
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
