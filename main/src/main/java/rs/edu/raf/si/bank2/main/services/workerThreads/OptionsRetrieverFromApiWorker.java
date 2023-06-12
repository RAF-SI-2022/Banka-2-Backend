package rs.edu.raf.si.bank2.main.services.workerThreads;

import rs.edu.raf.si.bank2.main.services.OptionService;

public class OptionsRetrieverFromApiWorker extends Thread {
    private final OptionService optionService;

    public OptionsRetrieverFromApiWorker(OptionService optionService) {
        this.optionService = optionService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.optionService.updateAllOptionsInDb();
                sleep(900000); // 15min = 1000 * 60 * 15 = 900000
            } catch (Exception e) {
                // If any unexpected error occurs, we want to print it and to continue.
                // Exception must not crash this thread worker.
                 e.printStackTrace();
            }
        }
    }
}
