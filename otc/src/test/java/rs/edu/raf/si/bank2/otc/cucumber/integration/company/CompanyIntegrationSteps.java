package rs.edu.raf.si.bank2.otc.cucumber.integration.company;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.services.CompanyBankAccountService;
import rs.edu.raf.si.bank2.otc.services.CompanyService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompanyIntegrationSteps extends CompanyIntegrationTestConfig {
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
    Company company;
    String token;

    AtomicLong registrationNumber = new AtomicLong(System.currentTimeMillis());
    AtomicLong taxNumber = new AtomicLong(System.currentTimeMillis());

    String testId = "155333555333L";

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

    @Then("user gets all companies")
    public void userGetsAllCompanies() {
        try {
            mockMvc.perform(get("/api/company")
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
                .registrationNumber(String.valueOf(this.registrationNumber.getAndIncrement()))
                .taxNumber(String.valueOf(this.taxNumber.getAndIncrement()))
                .activityCode("11111111111112")
                .address("Sekspirova 6")
                .contactPersons(new ArrayList<>())
                .bankAccounts(new ArrayList<>())
                .build();
        this.company = this.companyService.save(this.company);
    }

    @Then("user gets all accounts for company")
    public void userGetsAllAccountsForCompany() {
        try {
            mockMvc.perform(get("/api/company/accounts/" + this.company.getId())
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

    @Then("user gets company by id")
    public void userGetsCompanyById() {
        try {
            mockMvc.perform(get("/api/company/" + this.company.getId())
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

    @Then("user gets company by name")
    public void userGetsCompanyByName() {
        try {
            mockMvc.perform(get("/api/company/name/" + this.company.getName())
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

    @Then("user gets company by registration number")
    public void userGetsCompanyByRegistrationNumber() {
        try {
            mockMvc.perform(get("/api/company/registrationNumber/" + this.company.getRegistrationNumber())
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
    @Then("user gets company by tax number")
    public void userGetsCompanyByTaxNumber() {
        try {
            mockMvc.perform(get("/api/company/taxNumber/" + this.company.getTaxNumber())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Then("user creates company")
    public void userCreatesCompany() {
        try {
            mockMvc.perform(post("/api/company/create")
                            .contentType("application/json")
                            .content(String.format("""
                                {
                                    "id": "%s",
                                    "name": "My new company",
                                    "registrationNumber": "%s",
                                    "taxNumber": "%s",
                                    "activityCode": "555333",
                                    "address": "Topolska 18, soba podstanara"
                                }
                                """, this.registrationNumber.getAndIncrement(), this.registrationNumber.getAndIncrement(), this.taxNumber.getAndIncrement()))
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user adds contacts and accounts for company")
    public void userAddsContactsAndAccountsForCompany() {
        String companyID = this.company.getId();
        try {
            mockMvc.perform(post("/api/company/add")
                            .contentType("application/json")
                            .content(String.format("""
                                {
                                    "id": "%s",
                                    "contactPeople": [],
                                    "companyBankAccounts": []
                                }
                                """, companyID))
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user edits company")
    public void userEditsCompany() {

        try {
            String companyID = this.company.getId();
            mockMvc.perform(post("/api/company/edit")
                            .contentType("application/json")
                            .content(String.format("""
                                {
                                    "id": "%s",
                                    "name": "My new company - changed name",
                                    "registrationNumber": "%s",
                                    "taxNumber": "%s",
                                    "activityCode": "555333",
                                    "address": "Topolska 18, soba podstanara",
                                    "contactPersons" : [],
                                    "bankAccounts" : []
                                }
                                """, companyID, this.registrationNumber.getAndIncrement(), this.taxNumber.getAndIncrement()))
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("company does not exist in db")
    public void companyDoesNotExistInDb() {
        Optional<Company> companyOptional = this.companyService.getCompanyById(testId);
        companyOptional.ifPresent(value -> this.companyService.delete(value));
    }

    @Then("user cannot get company by id because it does not exist")
    public void userCannotGetCompanyByIdBecauseItDoesNotExist() {
        try {
            mockMvc.perform(get("/api/company/" + testId)
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user cannot get bank accounts for company because it does not exist")
    public void userCannotGetBankAccountsForCompanyBecauseItDoesNotExist() {
        try {
            mockMvc.perform(get("/api/company/accounts/" + testId)
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
