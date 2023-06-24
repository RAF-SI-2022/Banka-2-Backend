package rs.edu.raf.si.bank2.client.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.client.services.MailingService;
import rs.edu.raf.si.bank2.client.services.PaymentService;

@Component
public class BootstrapData implements CommandLineRunner {

    private final MailingService mailingService;
    private final PaymentService paymentService;

    @Autowired
    public BootstrapData(MailingService mailingService, PaymentService paymentService) {
        this.mailingService = mailingService;
        this.paymentService = paymentService;

        this.mailingService.getRegistrationCodes().add("1934");
        this.mailingService.getRegistrationCodes().add("1358");
        this.mailingService.getRegistrationCodes().add("5743");
        this.mailingService.getRegistrationCodes().add("4325");
        this.mailingService.getRegistrationCodes().add("2368");
        this.mailingService.getRegistrationCodes().add("2342");
        this.mailingService.getRegistrationCodes().add("2357");
        this.mailingService.getRegistrationCodes().add("3341");
        this.mailingService.getRegistrationCodes().add("1231");

        this.paymentService.getExchangeRates().put("RSD-USD", 0.0093);
        this.paymentService.getExchangeRates().put("RSD-EUR", 0.0085);
        this.paymentService.getExchangeRates().put("USD-RSD", 107.90);
        this.paymentService.getExchangeRates().put("USD-EUR", 0.92);
        this.paymentService.getExchangeRates().put("EUR-RSD", 117.29);
        this.paymentService.getExchangeRates().put("EUR-USD", 1.09);
    }

    @Override
    public void run(String... args) {}
}
