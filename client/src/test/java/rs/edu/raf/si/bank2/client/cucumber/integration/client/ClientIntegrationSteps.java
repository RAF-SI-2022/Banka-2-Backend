package rs.edu.raf.si.bank2.client.cucumber.integration.client;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.services.BalanceService;
import rs.edu.raf.si.bank2.client.services.ClientService;
import rs.edu.raf.si.bank2.client.services.UserService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        if (testClient.isEmpty()){
            Client newClient = new Client("Test","Testic",
                    "b-day", "nonb", "test@gmail.com", "123123123",
                    "addres", "password", new ArrayList<>());
            clientRepository.save(newClient);
        }
    }
    @When("test client is logged in")
    public void test_client_is_logged_in(){
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
            MvcResult mvcResult = mockMvc.perform(
                            get("/api/client")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

}
