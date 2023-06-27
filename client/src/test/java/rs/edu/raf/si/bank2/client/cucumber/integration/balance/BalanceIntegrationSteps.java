package rs.edu.raf.si.bank2.client.cucumber.integration.balance;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.dto.CommunicationDto;
import rs.edu.raf.si.bank2.client.dto.DevizniRacunDto;
import rs.edu.raf.si.bank2.client.dto.PoslovniRacunDto;
import rs.edu.raf.si.bank2.client.dto.TekuciRacunDto;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
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
    protected MockMvc mockMvc;

    @Autowired
    DevizniRacunRepository devizniRacunRepository;

    @Autowired
    TekuciRacunRepository tekuciRacunRepository;

    @Autowired
    PoslovniRacunRepository poslovniRacunRepository;

    @Autowired
    UserCommunicationService userCommunicationService;

    protected static String token;

    protected static String testClientId;

    ObjectMapper mapper = new ObjectMapper();

    @Given("test client is logged in")
    public void test_client_is_logged_in() throws JsonProcessingException {
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
        testClientId =
                clientRepository.findClientByEmail("test@gmail.com").get().getId();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("anesic3119rn+banka2backend+admin@raf.rs");
        loginRequest.setPassword("admin");
        String userJsonBody = mapper.writeValueAsString(loginRequest);
        CommunicationDto communicationDto =
                userCommunicationService.sendPostLike("/auth/login", userJsonBody, null, "POST");
        String[] split = communicationDto.getResponseMsg().split("\"");
        token = split[3];
    }

    @Then("get all tekuci racun")
    public void get_all_tekuci_racun() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/tekuci")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all tekuci failed");
        }
    }

    @Then("get all poslovni racuni")
    public void get_all_poslovni_racuni() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/poslovni")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all poslovni failed");
        }
    }

    @Then("get all tekuci devizni")
    public void get_all_tekuci_devizni() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/devizni")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all devizni failed");
        }
    }

    @Then("get all client balances")
    public void get_all_client_balances() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/forClient/test@gmail.com")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all client balances failed");
        }
    }

    @Then("open tekuci racun")
    public void open_tekuci_racun()
            throws io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
        TekuciRacunDto creditRequestDto = new TekuciRacunDto(testClientId, 1L, "USD", BalanceType.STEDNI, 1, 1.0);

        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditRequestDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/balance/openTekuciRacun")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get tekuci failed");
        }
    }

    @Then("open devizni racun")
    public void open_devizni_racun()
            throws io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
        DevizniRacunDto creditRequestDto =
                new DevizniRacunDto(testClientId, 1L, "USD", BalanceType.STEDNI, 1, 1.0, new ArrayList<>());
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditRequestDto);

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/balance/openDevizniRacun")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get devizni failed");
        }
    }

    @Then("open poslovni racun")
    public void open_poslovni_racun()
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
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get poslovni failed failed");
        }
    }

    @Then("get devizni racun")
    public void get_devizni_racun() {
        List<DevizniRacun> devizni = devizniRacunRepository.findAll();
        if (devizni.size() != 0) {
            try {
                MvcResult mvcResult = mockMvc.perform(
                                get("/api/balance/devizni/" + devizni.get(0).getId())
                                        .header("Content-Type", "application/json")
                                        .header("Access-Control-Allow-Origin", "*")
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                fail("Get all devizni failed");
            }
        }
    }

    @Then("get tekuci racun")
    public void get_tekuci_racun() {
        List<TekuciRacun> tekuci = tekuciRacunRepository.findAll();
        if (tekuci.size() != 0) {
            try {
                MvcResult mvcResult = mockMvc.perform(
                                get("/api/balance/tekuci/" + tekuci.get(0).getId())
                                        .header("Content-Type", "application/json")
                                        .header("Access-Control-Allow-Origin", "*")
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Get all tekuci failed");
            }
        }
    }

    @Then("get poslovni racun")
    public void get_poslovni_racun() {
        List<PoslovniRacun> poslovni = poslovniRacunRepository.findAll();
        if (poslovni.size() != 0) {
            try {
                MvcResult mvcResult = mockMvc.perform(
                                get("/api/balance/poslovni/" + poslovni.get(0).getId())
                                        .header("Content-Type", "application/json")
                                        .header("Access-Control-Allow-Origin", "*")
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                fail("Get all poslovni failed");
            }
        }
    }

    @Then("try to get nonexistent devizni")
    public void try_to_get_nonexistent_devizni() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/devizni/13212321")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all devizni failed");
        }
    }

    @Then("try to get nonexistent tekuci")
    public void try_to_get_nonexistent_tekuci() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/tekuci/13213123")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Get all tekuci failed");
        }
    }

    @Then("try to get nonexistent poslovni")
    public void try_to_get_nonexistent_poslovni() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/balance/poslovni/1321312312")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all poslovni failed");
        }
    }
}
