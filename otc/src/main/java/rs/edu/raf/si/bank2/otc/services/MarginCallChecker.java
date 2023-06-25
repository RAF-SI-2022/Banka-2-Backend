package rs.edu.raf.si.bank2.otc.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MarginCallChecker {
    @Scheduled(cron = "0 0 12 * * ?") // Runs every day at 12:00 PM
    public void runCronJob() {

        System.out.println("Running cron job..."); // todo enable-uj cron job da zapravo radi
    }
}
