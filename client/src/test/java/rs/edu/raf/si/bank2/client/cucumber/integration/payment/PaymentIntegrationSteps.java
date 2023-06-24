package rs.edu.raf.si.bank2.client.cucumber.integration.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import rs.edu.raf.si.bank2.client.services.BalanceService;
import rs.edu.raf.si.bank2.client.services.UserService;

public class PaymentIntegrationSteps extends PaymentIntegrationTestConfig {

    @Autowired
    BalanceService balanceService;

    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;


}
