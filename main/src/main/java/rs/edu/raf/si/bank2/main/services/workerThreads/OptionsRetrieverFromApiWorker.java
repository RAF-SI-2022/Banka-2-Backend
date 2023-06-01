package rs.edu.raf.si.bank2.main.services.workerThreads;

import rs.edu.raf.si.bank2.main.models.mariadb.Option;
import rs.edu.raf.si.bank2.main.services.OptionService;
import java.util.ArrayList;
import java.util.List;

public class OptionsRetrieverFromApiWorker extends Thread {
    private final OptionService optionService;
    public OptionsRetrieverFromApiWorker(OptionService optionService) {
        this.optionService = optionService;
    }

    @Override
    public void run() {
        while(true) {
            try {
                List<Option> optionList = this.getOptionsFromApi();
                this.insertOptionsInDb(optionList);
                sleep(900000); //15min = 1000 * 60 * 15 = 900000
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Option> getOptionsFromApi() {

        //Getting options for stocks with symbols: AAPL, GOOGL, AMZN, TSLA, NFLX
        List<Option> appleOptions = this.optionService.getFromExternalApi("AAPL", ""); // always sending without date, because it is irrelevant
        List<Option> googleOptions = this.optionService.getFromExternalApi("GOOGL", "");
        List<Option> amazonOptions = this.optionService.getFromExternalApi("AMZN", "");
        List<Option> teslaOptions = this.optionService.getFromExternalApi("TSLA", "");
        List<Option> netflixOptions = this.optionService.getFromExternalApi("NFLX", "");

        List<Option> optionList = new ArrayList<>();
        optionList.addAll(appleOptions);
        optionList.addAll(googleOptions);
        optionList.addAll(amazonOptions);
        optionList.addAll(teslaOptions);
        optionList.addAll(netflixOptions);

        return optionList;
    }

    private void insertOptionsInDb(List<Option> optionList) {
        this.optionService.deleteAll();
        this.optionService.saveAll(optionList);
    }
}
