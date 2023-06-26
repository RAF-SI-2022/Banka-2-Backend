package rs.edu.raf.si.bank2.otc.cucumber.integration.marginTransactionFailures;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MarginTransactionFailuresIntegrationSteps extends MarginTransactionFailuresIntegrationTestConfig {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    MarginTransactionRepository marginTransactionRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    MvcResult result;

    String transactionId = "900";

    @Given("user is logged in")
    public void user_is_logged_in(){
        try {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("anesic3119rn+banka2backend+admin@raf.rs");
            loginRequest.setPassword("admin");
            String userJsonBody = mapper.writeValueAsString(loginRequest);
            CommunicationDto communicationDto =
                    userCommunicationService.sendPostLike("/auth/login", userJsonBody, null, "POST");
            String[] split = communicationDto.getResponseMsg().split("\"");
            System.out.println(split);
            token = split[3];

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(token);
    }


    @Then("user fails to get transaction by id")
    public void user_fails_to_get_transaction_by_id() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/" + transactionId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}