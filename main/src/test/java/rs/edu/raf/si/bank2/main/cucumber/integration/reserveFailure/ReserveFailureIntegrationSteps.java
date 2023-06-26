package rs.edu.raf.si.bank2.main.cucumber.integration.reserveFailure;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.dto.ReserveDto;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;
import rs.edu.raf.si.bank2.main.services.OptionService;
import rs.edu.raf.si.bank2.main.services.OrderService;
import rs.edu.raf.si.bank2.main.services.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReserveFailureIntegrationSteps extends ReserveFailureIntegrationTestConfig {
    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    protected OptionService optionService;

    @Autowired
    private UserOptionRepository userOptionRepository;

    @Autowired
    private UserStocksRepository userStocksRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRepository exchangeRepository;

    private Stock stock;
    private Option option;
    private UserOption userOption;

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

    @Then("option to reserve not found")
    public void option_to_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(1L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/reserveOption")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("option to reserve not found failed");
        }
    }

    @Then("option to undo reserve not found")
    public void option_to_undo_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(1L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/undoReserveOption")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("option to undo reserve not found failed");
        }
    }

    @Then("stock to reserve not found")
    public void stock_to_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(1L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/reserveStock")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("stock to reserve not found failed");
        }
    }

    @Then("stock to undo reserve not found")
    public void stock_to_undo_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(1L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/undoReserveStock")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("stock to undo reserve not found failed");
        }
    }

    @Then("future to reserve not found")
    public void future_to_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(1L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/reserveFuture")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("future to reserve not found failed");
        }
    }

    @Then("future to undo reserve not found")
    public void future_to_undo_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(1L, 999L, 1);
        reserveDto.setFutureStorage("0,1,2,3,4,5,6,7,8,9");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/undoReserveFuture")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("option to reserve not found failed");
        }
    }


    @Then("balance to reserve not found")
    public void balance_to_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(99L, 999L, 1);
//        reserveDto.setFutureStorage("0,1,2,3,4,5,6,7,8,9");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/reserveMoney")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("balance to reserve not found");
        }
    }

    @Then("balance to undo reserve not found")
    public void balance_to_undo_reserve_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(99L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/undoReserveMoney")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("balance to undo reserve not found failed");
        }
    }



    @Then("while finalizing stock user not found")
    public void while_finalizing_stock_user_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(99L, 999L, 1);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/finalizeStock")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("balance to undo reserve not found failed");
        }
    }

    @Then("while finalizing option user not found")
    public void while_finalizing_option_user_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(99L, 999L, 1);
        reserveDto.setFutureStorage("asdf,asdf,asdf");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/finalizeOption")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("balance to undo reserve not found failed");
        }
    }

    @Then("while finalizing future user not found")
    public void while_finalizing_future_user_not_found() throws JsonProcessingException {
        ReserveDto reserveDto = new ReserveDto(99L, 999L, 1);
        reserveDto.setFutureStorage("asdf,asdf,asdf");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reserveDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/reserve/finalizeFuture")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("balance to undo reserve not found failed");
        }
    }

}
