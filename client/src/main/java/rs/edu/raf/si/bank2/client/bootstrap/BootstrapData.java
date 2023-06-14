package rs.edu.raf.si.bank2.client.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.client.services.ClientService;


@Component
public class BootstrapData implements CommandLineRunner {



    @Autowired
    public BootstrapData() {
    }

    @Override
    public void run(String... args){
    }

}
