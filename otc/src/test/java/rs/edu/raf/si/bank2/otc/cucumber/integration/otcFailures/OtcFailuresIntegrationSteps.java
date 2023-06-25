package rs.edu.raf.si.bank2.otc.cucumber.integration.otcFailures;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.TransactionElementRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

public class OtcFailuresIntegrationSteps extends OtcFailuresIntegrationTestConfig {

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
    String elementId = "501";

    /**
     * MvcResult of the last executed mock request.
     */
    MvcResult result;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    TransactionElementRepository transactionElementRepository;

    @Given("user without permission logs in")
    public void user_without_permission_logs_in() {

        try {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("powerless@gmail.com");
            loginRequest.setPassword("powerless");
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
                .transactionElements(Arrays.asList(
                        TransactionElement.builder().id("502").build(),
                        TransactionElement.builder().id("503").build()))
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

    @Then("unauthorized user fails to get all contracts")
    public void unauthorized_user_fails_to_get_all_contracts() throws Exception {

        result = mockMvc.perform(get("/api/otc")
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to get all contracts owned by that company")
    public void unauthorized_user_fails_to_get_all_contracts_owned_by_that_company() throws Exception {

        result = mockMvc.perform(get("/api/otc/byCompany/" + companyId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to get contract with specified contract id")
    public void unauthorized_user_fails_to_get_contract_with_specified_contract_id() throws Exception {

        result = mockMvc.perform(get("/api/otc/" + contractId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to open contract")
    public void unauthorized_user_fails_to_open_contract() throws Exception {

        result = mockMvc.perform(post("/api/otc/open")
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
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to edit contract")
    public void unauthorized_user_fails_to_edit_contract() throws Exception {

        result = mockMvc.perform(patch("/api/otc/edit")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "companyId": "500",
                                          "contractStatus": "DRAFT",
                                          "contractNumber": "300",
                                          "description": "edited description"
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to finalize contract by id")
    public void unauthorized_user_fails_to_finalize_contract_by_id() throws Exception {

        result = mockMvc.perform(patch("/api/otc/finalize/" + contractId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to delete contract by id")
    public void unauthorized_user_fails_to_delete_contract_by_id() throws Exception {

        result = mockMvc.perform(delete("/api/otc/delete/" + contractId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @When("elements exist in database")
    public void elements_exist_in_database() {

        TransactionElement transactionElement1 =
                TransactionElement.builder().id("501").build();
        TransactionElement transactionElement2 = TransactionElement.builder().build();
        TransactionElement transactionElement3 = TransactionElement.builder().build();

        transactionElementRepository.save(transactionElement1);
        transactionElementRepository.save(transactionElement2);
        transactionElementRepository.save(transactionElement3);
    }

    @Then("unauthorized user fails to get all elements")
    public void unauthorized_user_fails_to_get_all_elements() throws Exception {

        result = mockMvc.perform(get("/api/otc/elements")
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to get element by id")
    public void unauthorized_user_fails_to_get_element_by_id() throws Exception {

        result = mockMvc.perform(get("/api/otc/element/" + elementId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to get elements for contract")
    public void unauthorized_user_fails_to_get_elements_for_contract() throws Exception {

        result = mockMvc.perform(get("/api/otc/contract_elements/" + contractId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to add element to contract")
    public void unauthorized_user_fails_to_add_element_to_contract() throws Exception {

        result = mockMvc.perform(post("/api/otc/add_element")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "contractId": "500",
                                          "elementId": "504",
                                          "buyOrSell": "DRAFT",
                                          "transactionElement": "STOCK",
                                          "balance": "DRAFT",
                                          "currency": "string",
                                          "amount": 0,
                                          "priceOfOneElement": 0,
                                          "userId": 0,
                                          "mariaDbId": 0,
                                          "futureStorageField": "string"
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }

    @Then("unauthorized user fails to remove element from contract")
    public void unauthorized_user_fails_to_remove_element_from_contract() throws Exception {

        result = mockMvc.perform(delete("/api/otc/remove_element/" + contractId + "/" + elementId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertNotNull(result, "Json is not null");
    }
}
