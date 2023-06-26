package rs.edu.raf.si.bank2.client.cucumber.integration.client;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.services.ClientService;

public class ClientIntegrationSteps extends ClientIntegrationTestConfig {

    @Autowired
    ClientService clientService;

    @Autowired
    ClientRepository clientRepository;

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

    @Then("get all clients")
    public void get_all_clients() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/client")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Get all clients failed");
        }
    }

    @Then("get mail from token")
    public void get_mail_from_token() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/client/mailFromToken")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("Mail from token failed");
        }
    }

    @Then("get user by id")
    public void get_user_by_id() {
        Optional<Client> testClient = clientRepository.findClientByEmail("test@gmail.com");
        if (testClient.isEmpty()) fail("Nekako test user nije vise u bazi");

        try {
            MvcResult mvcResult = mockMvc.perform(
                            get("/api/client/" + testClient.get().getId())
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User by id failed");
        }
    }

    @Then("create user")
    public void create_user() throws JsonProcessingException {
        Optional<Client> newTestClient = clientRepository.findClientByEmail("new@gmail.com");
        newTestClient.ifPresent(client -> clientRepository.deleteById(client.getId()));

        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/client/createClient")
                            .contentType("application/json")
                            .content(
                                    """
                                    {
                                        "name": "new",
                                        "lastname": "new",
                                        "dateOfBirth": "new",
                                        "gender": "new",
                                        "email": "new@gmail.com",
                                        "telephone": "+1234567890",
                                        "address": "123 Main Street, City ABC",
                                        "password": "new"
                                    }
                                    """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @Then("sendToken")
    public void send_token() {
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/client/sendToken/banka2backend@gmail.com")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @Then("get nonexistent client")
    public void get_nonexistent_client() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/client/1231332")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("Nonexistent client by id failed");
        }
    }

    @Then("bad login credentials")
    public void bad_login_credentials() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/client/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "mmmm@gmail.com",
                                                      "password": "mmmm"
                                                    }
                                                    """))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to login");
        }
    }

    @Then("checkToken is valid")
    public void check_token_is_valid() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/client/checkToken/1358")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @Then("checkToken is not valid")
    public void check_token_is_not_valid() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/client/checkToken/1111")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @Then("deleteTestUsers")
    public void deleteTestUsers() {
        Optional<Client> newTestClient = clientRepository.findClientByEmail("new@gmail.com");
        newTestClient.ifPresent(client -> clientRepository.deleteById(client.getId()));
        Optional<Client> testClient = clientRepository.findClientByEmail("test@gmail.com");
        testClient.ifPresent(client -> clientRepository.deleteById(client.getId()));
    }
}
