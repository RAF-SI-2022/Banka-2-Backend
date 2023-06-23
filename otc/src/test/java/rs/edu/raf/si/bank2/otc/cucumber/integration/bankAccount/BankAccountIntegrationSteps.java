package rs.edu.raf.si.bank2.otc.cucumber.integration.bankAccount;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.exceptions.BankAccountNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.services.CompanyBankAccountService;
import rs.edu.raf.si.bank2.otc.services.CompanyService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankAccountIntegrationSteps extends BankAccountIntegrationTestConfig {

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

    @Autowired
    CompanyBankAccountService bankAccountService;

    @Autowired
    CompanyService companyService;

    CompanyBankAccount account;
    Company company;
    String token;

    @Given("user logs in")
    public void user_logs_in() {
        this.token = null;
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "anesic3119rn+banka2backend+admin@raf.rs",
                                                      "password": "admin"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            this.token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        } catch (Exception e) {
            fail("Test user failed to login");
        }
    }

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotEquals(this.token, null);
            assertNotEquals(this.token, "");
        } catch (Exception e) {
            fail("User token null or empty - not logged in properly");
        }
    }

    @Given("bank account exists in database")
    public void bank_account_exists_in_database() {
        Long bankAccountId = 123L;
        try{
            this.account = this.bankAccountService.getBankAccountById(bankAccountId.toString());
        } catch (BankAccountNotFoundException e){
            CompanyBankAccount account = CompanyBankAccount.builder()
                    .id(bankAccountId.toString())
                    .accountNumber("12345")
                    .currency("USD")
                    .bankName("test bank")
                    .build();
            this.account = this.bankAccountService.save(account);
        }
    }

    @Then("user gets bank account by id")
    public void userGetsBankAccountById() {
        try {
            mockMvc.perform(get("/api/bankaccount/" + this.account.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("company exists in db")
    public void company_exists_in_db() {
        Long companyId = 112L;
        Optional<Company> companyOptional = this.companyService.getCompanyById(companyId.toString());
        if(companyOptional.isPresent()) {
            this.company = companyOptional.get();
            return;
        }
        this.company = Company.builder()
                .id(companyId.toString())
                .name("Company2")
                .registrationNumber("11111111111112")
                .taxNumber("11111111111112")
                .activityCode("11111111111112")
                .address("Sekspirova 6")
                .contactPersons(new ArrayList<>())
                .bankAccounts(new ArrayList<>())
                .build();
        this.company = this.companyService.save(this.company);
    }

    @Then("user gets accounts by company id")
    public void userGetsAccountsByCompanyId() {
        try {
            mockMvc.perform(get("/api/bankaccount/company/" + this.company.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user creates account for company")
    public void userCreatesAccountForCompany() {
        try {
            mockMvc.perform(post("/api/bankaccount/" + this.company.getId())
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "id": "",
                                    "accountNumber": "555",
                                    "currency": "USD",
                                    "bankName": "Banka2 test"
                                }
                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user edits account for company")
    public void userEditsAccountForCompany() {
        try {
            mockMvc.perform(post("/api/bankaccount/edit")
                            .contentType("application/json")
                            .content(
                                    """
                                {
                                    "id": "123",
                                    "accountNumber": "556",
                                    "currency": "USD",
                                    "bankName": "Banka2 test novo ime"
                                }
                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user deletes account for company")
    public void userDeletesAccountForCompany() {
        try {
            mockMvc.perform(delete("/api/bankaccount/" + this.account.getId() + "/" + this.company.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
