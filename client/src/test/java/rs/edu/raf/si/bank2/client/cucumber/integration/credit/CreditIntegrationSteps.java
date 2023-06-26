package rs.edu.raf.si.bank2.client.cucumber.integration.credit;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.dto.CreditDto;
import rs.edu.raf.si.bank2.client.dto.CreditRequestDto;
import rs.edu.raf.si.bank2.client.dto.TekuciRacunDto;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.models.mongodb.Credit;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.CreditRepository;
import rs.edu.raf.si.bank2.client.services.BalanceService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreditIntegrationSteps extends CreditIntegrationTestConfig {

    @Autowired
    BalanceService balanceService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    protected CreditRepository creditRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;

    @Given("test client exists in db")
    public void user_logged_in() {
        Optional<Client> testClient = clientRepository.findClientByEmail("test@gmail.com");
        if (testClient.isEmpty()) {
            Client newClient = new Client(
                    "Test",
                    "Testic",
                    "b-day",
                    "nonb",
                    "test@gmail.com",
                    "123123123",
                    "addres",
                    "password",
                    new ArrayList<>());
            clientRepository.save(newClient);
        }
    }

    @When("test client is logged in")
    public void test_client_is_logged_in() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/client/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "test@gmail.com",
                                                      "password": "password"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

            System.out.println(token);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @Then("get credits for client")
    public void get_credits_for_client() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/credit/test@gmail.com")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Mail from token failed");
        }
    }

    @Then("get all payed interests")
    public void get_all_payed_interests() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/credit/interests/31213")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("all payed intersts failed");
        }
    }

    @Then("request credit")
    public void request_credit() throws JsonProcessingException {

        CreditRequestDto creditRequestDto =
                new CreditRequestDto("asdf", 1.0, "asdf", 1.0, true, "asfd", "asfd", 1, "asdf");
        String body = new ObjectMapper().writeValueAsString(creditRequestDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/credit/request")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("request credit failed");
        }
    }

    @Then("get all waiting")
    public void get_all_waiting() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/credit")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all waiting failed");
        }
    }

    @Then("deleteTestUsers")
    public void deleteTestUsers() {
        Optional<Client> testClient = clientRepository.findClientByEmail("test@gmail.com");
        testClient.ifPresent(client -> clientRepository.deleteById(client.getId()));
    }

    @Given("there is a credit")
    public void there_is_a_credit() {
        Credit credit = new Credit(
                "test@gmail.com",
                "test",
                "test",
                1000.0,
                1000.0,
                1,
                10.0, // todo porveri dal je ok
                "test",
                "test",
                "test");
        creditRepository.save(credit);

        Optional<Client> testClient = clientRepository.findClientByEmail("test@gmail.com");
        balanceService.openTekuciRacun(new TekuciRacunDto(testClient.get().getId(), 1L, "USD", BalanceType.STEDNI, 1, 1.0));

    }

    @Then("pay this months interest")
    public void pay_this_months_interest() {
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/credit/pay/12312f1f1121")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("pay this months interes failed");
        }
    }

    @Then("approve request")
    public void approve_request() throws JsonProcessingException {
        CreditDto creditDto = new CreditDto("asdfasf", "13212331", "name", 1.0, 1, 1.0, "asdfasdf", "USD");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/credit/approve/asflka123123")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }

    @Then("deny request")
    public void deny_request() {
        try {
            MvcResult mvcResult = mockMvc.perform(patch("/api/credit/deny/f3121121")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }
}
