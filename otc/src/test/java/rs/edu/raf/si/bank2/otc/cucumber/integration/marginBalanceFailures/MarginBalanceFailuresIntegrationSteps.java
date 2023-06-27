package rs.edu.raf.si.bank2.otc.cucumber.integration.marginBalanceFailures;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;

public class MarginBalanceFailuresIntegrationSteps extends MarginBalanceFailuresIntegrationTestConfig {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    MarginBalanceRepository marginBalanceRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    MvcResult result;

    String nonExistentBalanceId = "999";

    @Given("user is logged in")
    public void user_is_logged_in() {
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

    @Then("user fails to get margin balance by id")
    public void user_fails_to_get_margin_balance_by_id() throws Exception {

        result = mockMvc.perform(get("/api/marginAccount/" + nonExistentBalanceId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Then("user fails to update margin balance by id")
    public void user_fails_to_update_margin_balance_by_id() throws Exception {

        result = mockMvc.perform(put("/api/marginAccount/" + nonExistentBalanceId)
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "accountType": "CASH",
                                          "currencyCode": "string",
                                          "listingGroup": null,
                                          "investedResources": 0,
                                          "loanedResources": 0,
                                          "maintenanceMargin": 0,
                                          "marginCall": true
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Then("user fails to delete margin balance by id")
    public void user_fails_to_delete_margin_balance_by_id() throws Exception {

        result = mockMvc.perform(delete("/api/marginAccount/" + nonExistentBalanceId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
