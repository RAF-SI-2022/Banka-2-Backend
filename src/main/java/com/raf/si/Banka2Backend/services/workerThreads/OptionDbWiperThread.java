package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.repositories.mariadb.OptionRepository;
import lombok.SneakyThrows;

public class OptionDbWiperThread extends Thread {

    private final OptionRepository optionRepository;

    public OptionDbWiperThread(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            optionRepository.deleteAll();
            System.out.println("Database wiped");
            Thread.sleep(86400000);
        }
    }
}
