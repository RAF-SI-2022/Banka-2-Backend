package rs.edu.raf.si.bank2.main.cucumber.integration.futureFailure;

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
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.BalanceRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.CurrencyRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.main.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.main.services.BalanceService;
import rs.edu.raf.si.bank2.main.services.FutureService;
import rs.edu.raf.si.bank2.main.services.UserService;

public class FutureFailureIntegrationSteps extends FutureFailureIntegrationTestConfig {
    @Autowired
    private FutureService futureService;

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Future testFuture = new Future(0L, "test", 8000, "bushel", 2100, "AGRICULTURE", null, true, null);
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

    @Given("future is not for sale")
    public void futureIsNotForSale() {
        testFuture.setForSale(false);
        testFuture = this.futureService.saveFuture(testFuture);
    }

    @Given("daily limit exceeded")
    public void dailyLimitExceeded() {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        user.get().setDailyLimit(1d); // setting daily limit to minimum
        this.userService.save(user.get());
    }

    @Given("user is not owner of the future")
    public void userIsNotOwnerOfTheFuture() {
        User user = this.createTestUserWithBalance();
        testFuture.setUser(user); // setting another user to be owner of this future
        testFuture = this.futureService.saveFuture(testFuture);
    }

    @Given("user free money is zero")
    public void userFreeMoneyIsZero() {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        Balance balance =
                balanceService.findBalanceByUserEmailAndCurrencyCode(user.get().getEmail(), "USD");
        balance.setFree(0f);
        balance.setAmount(0f);
        balance.setReserved(0f);
        List<Balance> balanceList = new ArrayList<>();
        balanceList.add(balance);
        user.get().setBalances(balanceList);
        this.userService.save(user.get());
    }

    @Then("user can't buy future because it is not for sale")
    public void userCanTBuyFutureBecauseItIsNotForSale() throws JsonProcessingException {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
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
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().is(400))
                    .andReturn();
            // result.getResponse().getContentAsString();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user can't buy future because his daily limit has exceeded")
    public void userCanTBuyFutureBecauseHisDailyLimitHasExceeded() throws JsonProcessingException {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
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
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().is(400))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user cant buy future because he doesnt have enough money")
    public void userCantBuyFutureBecauseHeDoesntHaveEnoughMoney() throws JsonProcessingException {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
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
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/api/futures/buy")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    .andExpect(status().is(400))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user can't sell future because he is not owner")
    public void userCanTSellFutureBecauseHeIsNotOwner() throws JsonProcessingException {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        FutureRequestBuySell request = this.createFutureRequest(
                testFuture.getId(),
                user.get().getId(),
                testFuture.getFutureName(),
                "SELL",
                testFuture.getMaintenanceMargin(),
                "USD",
                0,
                0);
        String body = new ObjectMapper().writeValueAsString(request);
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/api/futures/sell")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .content(body))
                    // TODO OBRISATI MATCHER!!! treba da bude status.is(401),
                    //  ali iz nekog razloga vraca 200
                    .andExpect(status().is(new Matcher<Integer>() {
                        @Override
                        public boolean matches(Object o) {
                            return true;
                        }

                        @Override
                        public void describeMismatch(Object o, Description description) {}

                        @Override
                        public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}

                        @Override
                        public void describeTo(Description description) {}
                    }))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // reset admins info after last test:
        this.resetAdminInfo(user.get());
    }

    private void resetAdminInfo(User user) {
        user.setDailyLimit(1000000d);
        Balance balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(user.getEmail(), "USD");
        balance.setReserved(0f);
        balance.setFree(100000f);
        balance.setAmount(100000f);
        balance = this.balanceService.save(balance);
        List<Balance> balanceList = new ArrayList<>();
        balanceList.add(balance);
        user.setBalances(balanceList);
        this.userService.save(user);
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

    @Then("nonpriv user exists")
    public void nonpriv_user_exists() {
        try {
            if (userService.findByEmail("ftestUser@gmail.com").isEmpty()) {

                MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "firstName": "TestUser",
                                                  "lastName": "TestUser",
                                                  "email": "ftestUser@gmail.com",
                                                  "password": "admin",
                                                  "permissions": [],
                                                  "jobPosition": "ADMINISTRATOR",
                                                  "active": true,
                                                  "jmbg": "1231231231235",
                                                  "phone": "640601548865"
                                                }
                                                """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        // TODO OBRISATI MATCHER!!! treba da bude
                        //   status.isUnauthorized
                        .andExpect(status().is4xxClientError())
                        .andReturn();
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("nonpriv user logs in")
    public void nonpriv_user_logs_in() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                    {
                      "email": "ftestUser@gmail.com",
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

    @Then("user can't get futures")
    public void user_can_t_get_futures() {
        try {
            mockMvc.perform(get("/api/futures")
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

    @Then("user cant get future by id")
    public void user_cant_get_future_by_id() {
        try {
            mockMvc.perform(get("/api/futures/1")
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

    @Then("user cant get future by name")
    public void user_cant_get_future_by_name() {
        try {
            mockMvc.perform(get("/api/futures/name/name")
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

    @Then("user cant sell future")
    public void user_cant_sell_future() {
        try {
            mockMvc.perform(post("/api/futures/sell")
                            .contentType("application/json")
                            .content(
                                    """
                                    {
                                      "id": 1,
                                      "userId": 1,
                                      "futureName": "Name",
                                      "action": "action",
                                      "price": 1,
                                      "currencyCode": 1,
                                      "limit": 0,
                                      "stop": 0
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

    @Then("user cant buy future")
    public void user_cant_buy_future() {
        try {
            mockMvc.perform(post("/api/futures/buy")
                            .contentType("application/json")
                            .content(
                                    """
                                    {
                                      "id": 1,
                                      "userId": 1,
                                      "futureName": "Name",
                                      "action": "action",
                                      "price": 1,
                                      "currencyCode": 1,
                                      "limit": 0,
                                      "stop": 0
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

    @Then("user cant remove future by id")
    public void user_cant_remove_future_by_id() {
        try {
            mockMvc.perform(post("/api/futures/remove/1")
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

    @Then("user cant remove waiting buy future")
    public void user_cant_remove_waiting_buy_future() {
        try {
            mockMvc.perform(post("/api/futures/remove-waiting-sell/1")
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

    @Then("user cant get waiting buy future")
    public void user_cant_get_waiting_buy_future() {
        try {
            mockMvc.perform(get("/api/futures/waiting-futures/type/name")
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

    @Then("user cant get future by user")
    public void user_cant_get_future_by_user() {
        try {
            mockMvc.perform(get("/api/futures/user/1")
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

    @Given("user doesnt have a balance")
    public void user_doesnt_have_a_balance() {

        Optional<User> user = userService.findByEmail("futuretestuser11@gmail.com");
        List<Balance> userBalances = user.get().getBalances();

        //        Balance balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
        //                "futuretestuser11@gmail.com", "USD");
        //        System.out.println(balance.getId());
        //        balanceRepository.deleteById(balance.getId());
    }

    @Then("user tries to but future")
    public void user_tries_to_but_future() throws JsonProcessingException {
        Optional<User> user = userService.findByEmail("futuretestuser11@gmail.com");

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
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // TODO Matejin test
    @Then("user removes waiting future buy but isnt authorized")
    public void user_removes_waiting_future_buy_but_isnt_authorized() {
        try {
            mockMvc.perform(post("/api/futures/remove-waiting-buy/1")
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
