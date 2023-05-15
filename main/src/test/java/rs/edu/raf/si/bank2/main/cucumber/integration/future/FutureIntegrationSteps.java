package rs.edu.raf.si.bank2.main.cucumber.integration.future;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.CurrencyRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.main.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.main.services.BalanceService;
import rs.edu.raf.si.bank2.main.services.FutureService;
import rs.edu.raf.si.bank2.main.services.UserService;

public class FutureIntegrationSteps extends FutureIntegrationTestConfig {

    @Autowired
    private FutureService futureService;

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Future testFuture = new Future(1L, "corn", 8000, "bushel", 2100, "AGRICULTURE", null, true, null);
    protected static User testUser;

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

    @Then("user gets all futures from database")
    public void user_gets_futures() {
        try {
            mockMvc.perform(get("/api/futures")
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

    // Test user gets currency by id
    @Then("user gets future by id from database")
    public void user_gets_future_by_id() {
        try {
            mockMvc.perform(get("/api/futures/" + testFuture.getId())
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

    @Then("user gets future by name from database")
    public void user_gets_future_by_name() {
        try {
            mockMvc.perform(get("/api/futures/name/" + testFuture.getFutureName())
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

    @Given("future is on sale")
    public void futureIsOnSale() {
        testFuture.setForSale(true);
        testFuture = this.futureService.saveFuture(testFuture);
    }

    @Given("future is owned by another user")
    public void futureIsOwnedByAnotherUser() {
        testUser = this.createTestUserWithBalance();
    }

    @Given("future is owned by this user")
    public void futureIsOwnedByThisUser() {
        Optional<User> user = this.userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        testFuture.setForSale(false);
        testFuture.setUser(user.get());
        testFuture = this.futureService.saveFuture(testFuture);
    }

    @Then("user buys future from company")
    public void userBuysFutureFromCompany() throws JsonProcessingException {

        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        Balance balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                "anesic3119rn+banka2backend+admin@raf.rs", "USD");
        Float oldFree = balance.getFree();

        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                user.get().getId(),
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "USD",
                0,
                0);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                "anesic3119rn+banka2backend+admin@raf.rs", "USD");
        assertEquals(
                Math.round(oldFree - testFuture.getMaintenanceMargin().floatValue()), Math.round(balance.getFree()));
    }

    @Then("user buys future from company with null currency code")
    public void userBuysFutureFromCompany_with_null_currency_code() throws JsonProcessingException {

        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        Balance balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                "anesic3119rn+banka2backend+admin@raf.rs", "USD");
        Float oldFree = balance.getFree();

        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                user.get().getId(),
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "",
                0,
                0);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                "anesic3119rn+banka2backend+admin@raf.rs", "USD");
        assertEquals(
                Math.round(oldFree - testFuture.getMaintenanceMargin().floatValue()), Math.round(balance.getFree()));
    }

    @Then("user buys future from company with limit or stop")
    public void userBuysFutureFromCompanyWithLimitOrStop() throws JsonProcessingException {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");

        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                user.get().getId(),
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "USD",
                1900,
                2500);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user buys future from another user")
    public void userBuysFutureFromAnotherUser() throws JsonProcessingException {
        Optional<User> buyer = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        Balance buyerBalance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                "anesic3119rn+banka2backend+admin@raf.rs", "USD");
        Float buyerOldFree = buyerBalance.getFree();

        User seller = this.createTestUserWithBalance();
        Balance sellerBalance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(seller.getEmail(), "USD");
        Float sellerOldFree = sellerBalance.getFree();

        testFuture.setUser(seller);
        testFuture = this.futureService.saveFuture(testFuture);

        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                buyer.get().getId(),
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "USD",
                0,
                0);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        buyerBalance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                buyer.get().getEmail(), "USD");
        sellerBalance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(seller.getEmail(), "USD");
        assertEquals(
                Math.round(buyerOldFree - testFuture.getMaintenanceMargin().floatValue()),
                Math.round(buyerBalance.getFree()));
        assertEquals(
                Math.round(sellerOldFree + testFuture.getMaintenanceMargin().floatValue()),
                Math.round(sellerBalance.getFree()));
    }

    @Then("user buys future from another user with limit or stop")
    public void userBuysFutureFromAnotherUserWithLimitOrStop() throws JsonProcessingException {
        Optional<User> buyer = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        User seller = testUser;
        testFuture.setUser(seller);
        testFuture = this.futureService.saveFuture(testFuture);

        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                buyer.get().getId(),
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "USD",
                1900,
                2500);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user sells future")
    public void userSellsFuture() throws JsonProcessingException {
        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                null,
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "USD",
                0,
                0);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Optional<Future> f = this.futureService.findById(testFuture.getId());
        assertTrue(f.get().isForSale());
    }

    @Then("user sells future with limit or stop")
    public void userSellsFutureWithLimitOrStop() throws JsonProcessingException {
        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                null,
                testFuture.getFutureName(),
                "BUY",
                testFuture.getMaintenanceMargin(),
                "USD",
                2500,
                1900);
        String body = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(post("/api/futures/sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private FutureRequestBuySell createFutureRequest(
            Long id,
            Long userId,
            String futureName,
            String action,
            Integer price,
            String currencyCode,
            Integer limit,
            Integer stop) { // help method;
        FutureRequestBuySell request = new FutureRequestBuySell();
        request.setId(id);
        request.setUserId(userId);
        request.setFutureName(futureName);
        request.setAction(action);
        request.setPrice(price);
        request.setCurrencyCode(currencyCode);
        request.setLimit(limit);
        request.setStop(stop);
        return request;
    }

    private User createTestUserWithBalance() {
        Optional<User> optionalUser = this.userService.findByEmail("futuretestuser11@gmail.com");
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        List<Permission> permissions = new ArrayList<>();
        Permission p = new Permission(PermissionName.ADMIN_USER);
        p = this.permissionRepository.save(p);
        permissions.add(p);
        User user = User.builder()
                .firstName("User11")
                .lastName("Useric11")
                .email("futuretestuser11@gmail.com")
                .password("user11")
                .permissions(permissions)
                .jobPosition("ADMINISTRATOR")
                .active(true)
                .jmbg("4444555678112")
                .phone("065333494")
                .dailyLimit(1000000d)
                .build();
        user = this.userService.save(user);

        Balance balance = new Balance();
        balance.setUser(user);
        Optional<Currency> curr = this.currencyRepository.findCurrencyByCurrencyCode("USD");
        balance.setCurrency(curr.get());
        balance.setAmount(100000f);
        balance.setFree(100000f);
        balance.setReserved(0f);
        balance.setType(BalanceType.CASH);
        balance = this.balanceService.save(balance);

        List<Balance> balances = new ArrayList<>();
        balances.add(balance);
        user.setBalances(balances);
        return this.userService.save(user);
    }

    @Then("user gets waiting-futures")
    public void user_gets_waiting_futures() {
        try {
            mockMvc.perform(get("/api/futures/CALL/A")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets his futures")
    public void user_gets_his_futures() {
        try {
            mockMvc.perform(get("/api/futures/user/1")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*"))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user removes waiting future buy")
    public void user_removes_waiting_future_buy() {
        try {
            mockMvc.perform(post("/api/futures/remove-waiting-buy/-1")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*"))
                    .andExpect(status().isInternalServerError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user removes waiting future sell")
    public void user_removes_waiting_future_sell() {
        try {
            mockMvc.perform(post("/api/futures/remove-waiting-sell/-1")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*"))
                    .andExpect(status().isInternalServerError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user removes from market")
    public void user_removes_removes_from_market() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(post("/api/futures/remove/0")
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*"))
                        .andExpect(status().isInternalServerError())
                        .andReturn();
            });
            String expectedMessage =
                    "Request processing failed; nested exception is java.util.NoSuchElementException: No value present";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
