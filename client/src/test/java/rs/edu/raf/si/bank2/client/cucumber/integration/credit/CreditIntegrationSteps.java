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
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.dto.CreditDto;
import rs.edu.raf.si.bank2.client.dto.CreditRequestDto;
import rs.edu.raf.si.bank2.client.dto.TekuciRacunDto;
import rs.edu.raf.si.bank2.client.models.mongodb.*;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.CreditApproval;
import rs.edu.raf.si.bank2.client.repositories.mongodb.*;
import rs.edu.raf.si.bank2.client.services.BalanceService;

public class CreditIntegrationSteps extends CreditIntegrationTestConfig {

    @Autowired
    BalanceService balanceService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    protected CreditRepository creditRepository;

    @Autowired
    protected TekuciRacunRepository tekuciRacunRepository;

    @Autowired
    protected RacunStorageRepository racunStorageRepository;

    @Autowired
    protected CreditRequestRepository creditRequestRepository;

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
                new CreditRequestDto("test@gmail.com", 1.0, "asdf", 1.0, true, "asfd", "asfd", 1, "asdf");
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
        balanceService.openTekuciRacun(
                new TekuciRacunDto(testClient.get().getId(), 1L, "USD", BalanceType.STEDNI, 1, 1.0));
    }

    @Given("there is a request waiting to approve")
    public void there_is_a_request_waiting_to_approve() {
        Optional<Client> client = clientRepository.findClientByEmail("test@gmail.com");

        CreditRequest creditRequest = new CreditRequest(
                "CreditRequestTestForApprove",
                "test@gmail.com",
                CreditApproval.WAITING,
                1000.0,
                "Personal expenses",
                50.0,
                true,
                "City XYZ",
                "2 years",
                12,
                "1234567890");
        Optional<CreditRequest> appCreReq = creditRequestRepository.findById("CreditRequestTestForApprove");
        System.err.println(appCreReq);
        if (appCreReq.isEmpty()) {
            System.err.println("usli smo");
            System.err.println(creditRequestRepository.save(creditRequest));
        }

        TekuciRacun tekuciRacun = new TekuciRacun(
                "tekuciRacunTestBalance",
                "111111111",
                client.get().getId(),
                Balance.TEKUCI,
                1000.0,
                1000.0,
                1L,
                "2022-01-01",
                "2023-01-01",
                "USD",
                BalanceStatus.ACTIVE,
                BalanceType.STEDNI,
                1,
                10.0);
        racunStorageRepository.save(new RacunStorage("111111111", Balance.TEKUCI));
        tekuciRacunRepository.save(tekuciRacun);
        //        if (tekuciRacunRepository.findById("tekuciRacunTestBalance").isEmpty()) {
        //
        //        }
    }

    @Then("approve request")
    public void approve_request() throws JsonProcessingException {
        CreditDto creditDto = new CreditDto("test@gmail.com", "name", "111111111", 10.0, 1, 1.0, "asdfasdf", "USD");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/credit/approve/CreditRequestTestForApprove")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }

    @Then("pay this months interest")
    public void pay_this_months_interest() {
        List<Credit> credits = creditRepository.findAll();
        if (credits.size() != 0) {
            try {
                MvcResult mvcResult = mockMvc.perform(
                                post("/api/credit/pay/" + credits.get(0).getId())
                                        .header("Content-Type", "application/json")
                                        .header("Access-Control-Allow-Origin", "*")
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                e.printStackTrace();
                fail("pay this months interes failed");
            }
        }
    }

    @Given("there is a request waiting to deny")
    public void there_is_a_request_waiting_to_deny() {
        CreditRequest creditRequest = new CreditRequest(
                "CreditRequestTestForDeny",
                "test@gmail.com",
                CreditApproval.WAITING,
                1000.0,
                "Personal expenses",
                50.0,
                true,
                "City XYZ",
                "2 years",
                12,
                "1234567890");
        Optional<CreditRequest> denyCreReq = creditRequestRepository.findById("CreditRequestTestForDeny");
        if (denyCreReq.isEmpty()) {
            creditRequestRepository.save(creditRequest);
        }
    }

    @Then("deny request")
    public void deny_request() {
        try {
            MvcResult mvcResult = mockMvc.perform(patch("/api/credit/deny/CreditRequestTestForDeny")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }
}
