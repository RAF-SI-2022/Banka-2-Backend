package rs.edu.raf.si.bank2.client.cucumber.integration.balanceFailure;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.dto.CommunicationDto;
import rs.edu.raf.si.bank2.client.dto.DevizniRacunDto;
import rs.edu.raf.si.bank2.client.dto.PoslovniRacunDto;
import rs.edu.raf.si.bank2.client.dto.TekuciRacunDto;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BussinessAccountType;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.DevizniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PoslovniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.TekuciRacunRepository;
import rs.edu.raf.si.bank2.client.requests.LoginRequest;
import rs.edu.raf.si.bank2.client.services.ClientService;
import rs.edu.raf.si.bank2.client.services.UserCommunicationService;

public class BalanceIntegrationSteps extends BalanceIntegrationTestConfig {

    @Autowired
    ClientService clientService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    DevizniRacunRepository devizniRacunRepository;

    @Autowired
    TekuciRacunRepository tekuciRacunRepository;

    @Autowired
    PoslovniRacunRepository poslovniRacunRepository;

    protected static String token;

    protected static String testClientId;

    ObjectMapper mapper = new ObjectMapper();

    @Given("test client is logged in")
    public void test_client_is_logged_in() throws JsonProcessingException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("powerless@gmail.com");
        loginRequest.setPassword("powerless");
        String userJsonBody = mapper.writeValueAsString(loginRequest);
        CommunicationDto communicationDto =
                userCommunicationService.sendPostLike("/auth/login", userJsonBody, null, "POST");
        String[] split = communicationDto.getResponseMsg().split("\"");
        token = split[3];
    }

    @Then("get all tekuci racuni without perms")
    public void get_all_tekuci_racuni_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/tekuci")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all tekuci failed");
        }
    }

    @Then("get all devizni racuni without perms")
    public void get_all_devizni_racuni_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/devizni")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all devizni failed");
        }
    }

    @Then("get all poslovni racuni without perms")
    public void get_all_poslovni_racuni_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/poslovni")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all poslovni failed");
        }
    }

    @Then("get all for client without perms")
    public void get_all_for_client_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/forClient/temail.com")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all client balances failed");
        }
    }

    @Then("open devizni witout perms")
    public void open_devizni_witout_perms()
            throws io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
        DevizniRacunDto creditRequestDto =
                new DevizniRacunDto(testClientId, 1L, "USD", BalanceType.STEDNI, 1, 1.0, new ArrayList<>());
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditRequestDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/balance/openTekuciRacun")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get tekuci failed");
        }
    }

    @Then("open poslovni witout perms")
    public void open_poslovni_witout_perms()
            throws io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
        PoslovniRacunDto creditRequestDto = new PoslovniRacunDto(testClientId, 1L, "USD", BussinessAccountType.KUPOVNI);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditRequestDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/balance/openPoslovniRacun")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get tekuci failed");
        }
    }

    @Then("open tekuci witout perms")
    public void open_tekuci_witout_perms()
            throws io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
        TekuciRacunDto creditRequestDto = new TekuciRacunDto(testClientId, 1L, "USD", BalanceType.STEDNI, 1, 1.0);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditRequestDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/balance/openDevizniRacun")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get tekuci failed");
        }
    }

    @Then("get devizni without perms")
    public void get_devizni_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/poslovni/1")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all poslovni failed");
        }
    }

    @Then("get poslovni without perms")
    public void get_poslovni_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/poslovni/1123")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all poslovni failed");
        }
    }

    @Then("get tekuci without perms")
    public void get_tekuci_without_perms() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/tekuci/1")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Get all tekuci failed");
        }
    }
}
