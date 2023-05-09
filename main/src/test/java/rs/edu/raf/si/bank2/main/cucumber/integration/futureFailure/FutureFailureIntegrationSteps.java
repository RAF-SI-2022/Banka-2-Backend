package rs.edu.raf.si.bank2.main.cucumber.integration.futureFailure;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.CurrencyRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.main.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.main.services.BalanceService;
import rs.edu.raf.si.bank2.main.services.FutureService;
import rs.edu.raf.si.bank2.main.services.UserService;
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
                    .andExpect(status().is(401))
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
}
