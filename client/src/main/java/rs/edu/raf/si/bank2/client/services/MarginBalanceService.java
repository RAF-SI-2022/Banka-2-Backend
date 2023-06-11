package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.models.mongodb.MarginBalance;
import rs.edu.raf.si.bank2.client.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.MarginTransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MarginBalanceService {

    private final MarginBalanceRepository marginBalanceRepository;
    MarginTransactionRepository marginTransactionRepository;
    @Autowired
    public MarginBalanceService(MarginBalanceRepository marginBalanceRepository, MarginTransactionRepository marginTransactionRepository) {
        this.marginTransactionRepository = marginTransactionRepository;
        this.marginBalanceRepository = marginBalanceRepository;
    }

    public List<MarginBalance> getAllMarginBalances() {
        return marginBalanceRepository.findAll();
    }

    public MarginBalance getMarginBalanceById(String id) {
        Optional<MarginBalance> marginBalanceOptional = marginBalanceRepository.findById(id);
        return marginBalanceOptional.orElse(null);
    }

    public MarginBalance createMarginBalance(MarginBalance marginBalance) {
        return marginBalanceRepository.save(marginBalance);
    }

    public MarginBalance updateMarginBalance(MarginBalance marginBalance) {
        return marginBalanceRepository.save(marginBalance);
    }

    public void deleteMarginBalance(MarginBalance marginBalance) {
        marginBalanceRepository.delete(marginBalance);
    }
//    public MarginTransaction setupAccounts(MarginTransactionDto marginTransactionDto){
//        MarginTransaction marginTransaction = new MarginTransaction();
//        marginTransaction.setTransactionType(marginTransactionDto.getTransactionType());
//        marginTransaction.setMaintenanceMargin(marginTransactionDto.getMaintenanceMargin());
//        marginTransaction.setInterest(marginTransactionDto.getInterest());
//        marginTransaction.setAccountType(marginTransactionDto.getAccountType());
//        marginTransaction.setInitialMoney(marginTransactionDto.getInitialMoney());
//        marginTransaction.setOrderId(marginTransactionDto.getOrderId());
//        return marginTransactionRepository.save(marginTransaction);
//    }
}
