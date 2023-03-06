package rs.raf.demo.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.raf.demo.services.UserService;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public BootstrapData( UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading data");

        System.out.println("Data loaded");

    }
}
