package rs.edu.raf.si.bank2.client.cucumber.integration.credit;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.dto.CreditDto;
import rs.edu.raf.si.bank2.client.dto.CreditRequestDto;
import rs.edu.raf.si.bank2.client.dto.PaymentDto;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.services.BalanceService;

public class CreditIntegrationSteps extends CreditIntegrationTestConfig {

    @Autowired
    BalanceService balanceService;

    @Autowired
    private ClientRepository clientRepository;

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
            MvcResult mvcResult = mockMvc.perform(
                    get("/api/credit")
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

    @Given("pay this months interest")
    public void pay_this_months_interest() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/payment/pay/1613asfaew")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all waiting failed");
        }
    }

//    private String clientEmail;
//    private String name;
//    private String accountRegNumber;
//    private Double amount;
//    private Integer ratePercentage; // stopa na iznos
//    private Double monthlyRate; // koliko se mesecno placa
//    private String dueDate; // do kad se otplacuje
//    private String currency;

    @Given("approve request")
    public void approve_request() throws JsonProcessingException {
        CreditDto creditDto = new CreditDto("asdfasf", "13212331", "name", 1.0,
                1, 1.0, "asdfasdf", "USD");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(creditDto);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/payment/approve/asflka123123")
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

    @Given("deny request")
    public void deny_request() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            patch("/api/payment/approve/123d12d1")

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
