package com.raf.si.Banka2Backend.bootstrap;

import com.raf.si.Banka2Backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        //TODO: create admin user and save it in user repository;
    }
}
