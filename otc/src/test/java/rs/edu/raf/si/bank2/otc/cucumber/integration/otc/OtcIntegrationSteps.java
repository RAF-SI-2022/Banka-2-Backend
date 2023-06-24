package rs.edu.raf.si.bank2.otc.cucumber.integration.otc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OtcIntegrationSteps extends OtcIntegrationTestConfig {

    /**
     * Lock for synchronized operations on the token.
     */
    final Object tokenLock = new Object();

    @Autowired
    UserServiceInterface userServiceInterface;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    @Spy
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @InjectMocks
    AuthorisationServiceInterface authorisationServiceInterface;

    /**
     * User's password in plain form.
     */
    String pass = "12345";

    /**
     * Dummy user for testing.
     */
    User user;

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    CompanyRepository companyRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    String companyId = "500";
    String contractId = "500";

    /**
     * MvcResult of the last executed mock request.
     */
    MvcResult result;

    @Autowired
    ContactRepository contactRepository;

    @Given("user logs in")
    public void user_logs_in() {
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


    @When("contracts exist in database")
    public void contracts_exist_in_database() {

        Company company = Company.builder().id(companyId).build();

        Contract contract1 = Contract.builder()
                .id(contractId)
                .contractStatus(ContractElements.BUY)
                .contractNumber("1231321")
                .companyId(companyId)
                .build();
        Contract contract2 = Contract.builder()
                .contractStatus(ContractElements.BUY)
                .contractNumber("1231321")
                .companyId(companyId)
                .build();
        Contract contract3 = Contract.builder()
                .contractStatus(ContractElements.BUY)
                .contractNumber("1231321")
                .build();

        companyRepository.save(company);

        contactRepository.save(contract1);
        contactRepository.save(contract2);
        contactRepository.save(contract3);

    }

    @Then("user gets all contracts")
    public void user_gets_all_contracts() throws Exception {

        result = mockMvc.perform(get("/api/otc").contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
//        assertNotNull(result, "Json is not null");
    }

    @Then("user gets all contracts owned by that company")
    public void user_gets_all_contracts_owned_by_that_company() throws Exception {

        result = mockMvc.perform(get("/api/otc/byCompany/" + companyId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

//        assertNotNull(result, "Json is not null");
    }

    @Then("user gets contract with specified contract id")
    public void user_gets_contract_with_specified_contract_id() throws Exception {

        result = mockMvc.perform(get("/api/otc/" + contractId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("user opens contract")
    public void user_opens_contract() throws Exception {

        result = mockMvc.perform(post("/open")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "companyId": "500",
                                          "contractStatus": "DRAFT",
                                          "contractNumber": "300",
                                          "description": "string"
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

//        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());
//
//        assertNotNull(actualJson, "Json is not null");

        assertNotNull(result, "Json is not null");

    }
}
