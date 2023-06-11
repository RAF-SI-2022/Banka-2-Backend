package rs.edu.raf.si.bank2.client.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.si.bank2.client.models.mongodb.AccountType;
import rs.edu.raf.si.bank2.client.models.mongodb.ListingGroup;
import rs.edu.raf.si.bank2.client.models.mongodb.MarginBalance;
import rs.edu.raf.si.bank2.client.repositories.mongodb.MarginBalanceRepository;

@Component
public class BootstrapData implements CommandLineRunner {

    MarginBalanceRepository marginBalanceRepository;
    public void initializeMarginAccount(){
        for (ListingGroup listingGroup : ListingGroup.values()) {
            if(marginBalanceRepository.findMarginBalanceByListingGroup(listingGroup).isPresent()){
                continue;
            }
            MarginBalance oneBalance= MarginBalance.builder()
                    .accountType(AccountType.MARGIN)
                    .maintenanceMargin(2000.00)
                    .marginCall(false)
                    .currencyCode("USD")
                    .investedResources(300000.00)
                    .loanedResources(650.00)
                    .listingGroup(listingGroup)
                    .build();
            marginBalanceRepository.save(oneBalance);
            System.out.println("LOADED : " + listingGroup.name() + "ACCOUNT");
        }
    }

    @Autowired
    public BootstrapData(MarginBalanceRepository marginBalanceRepository) {
        this.marginBalanceRepository = marginBalanceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeMarginAccount();
    }
}
