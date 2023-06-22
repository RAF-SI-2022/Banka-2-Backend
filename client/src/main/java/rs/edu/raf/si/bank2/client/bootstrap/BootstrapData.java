package rs.edu.raf.si.bank2.client.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.client.services.MailingService;


@Component
public class BootstrapData implements CommandLineRunner {

    private final MailingService mailingService;


    @Autowired
    public BootstrapData(MailingService mailingService) {
        this.mailingService = mailingService;
        this.mailingService.getRegistrationCodes().add("1934");
        this.mailingService.getRegistrationCodes().add("1358");
        this.mailingService.getRegistrationCodes().add("5743");
        this.mailingService.getRegistrationCodes().add("4325");
        this.mailingService.getRegistrationCodes().add("2368");
        this.mailingService.getRegistrationCodes().add("2342");
        this.mailingService.getRegistrationCodes().add("2357");
        this.mailingService.getRegistrationCodes().add("3341");
        this.mailingService.getRegistrationCodes().add("1231");
    }

    @Override
    public void run(String... args){
    }

}
