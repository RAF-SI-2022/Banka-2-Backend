package rs.edu.raf.si.bank2.otc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.MarginTransactionDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.ListingGroup;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginBalance;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginTransaction;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionType;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarginTransactionService {
    private final MarginBalanceRepository marginBalanceRepository;
    private final UserCommunicationService userCommunicationService;
    MarginTransactionRepository marginTransactionRepository;

    @Autowired
    public MarginTransactionService(MarginBalanceRepository marginBalanceRepository, UserCommunicationService userCommunicationService, MarginTransactionRepository marginTransactionRepository) {
        this.userCommunicationService = userCommunicationService;
        this.marginTransactionRepository = marginTransactionRepository;
        this.marginBalanceRepository = marginBalanceRepository;
    }

    public void updateBalance(MarginBalance marginBalance, MarginTransaction marginTransaction) {
        if (marginTransaction.getTransactionType().equals(TransactionType.BUY)) {
            marginBalance.setInvestedResources(marginBalance.getInvestedResources() - marginTransaction.getInitialMargin());
            marginBalance.setLoanedResources(marginBalance.getLoanedResources() + marginTransaction.getLoanValue());
            marginBalance.setMaintenanceMargin(marginBalance.getMaintenanceMargin() + marginTransaction.getMaintenanceMargin());
        }
        else {
            marginBalance.setInvestedResources(marginBalance.getInvestedResources() + marginTransaction.getInitialMargin());
            marginBalance.setMaintenanceMargin(marginBalance.getMaintenanceMargin() - marginTransaction.getMaintenanceMargin());
            if (marginBalance.getInvestedResources() < 0) {
                marginBalance.setInvestedResources(0.0);
            }
            if (marginBalance.getMaintenanceMargin() < 0) {
                marginBalance.setMaintenanceMargin(0.0);
            }
        }
        marginBalanceRepository.save(marginBalance);
    }

    public String trim(String input) {
        String[] parts = input.split("\"responseMsg\":\"");
        String lastNumber = parts[1].split("\"")[0];
        return lastNumber;
    }

    public MarginTransaction makeTransaction(MarginTransactionDto marginTransactionDto, String email) {
        String response = trim(userCommunicationService.sendGet(null, "/orders/value/" + marginTransactionDto.getOrderId(), "main").getResponseMsg());
        double loanVal = Double.parseDouble(response) - marginTransactionDto.getInitialMargin();
        if (loanVal < 0) {
            loanVal = 0.0;
        }

        System.out.println(userCommunicationService.sendGet(null, "/orders/orderType/" + marginTransactionDto.getOrderId(), "main").getResponseMsg());
        String orderType = trim(userCommunicationService.sendGet(null, "/orders/orderType/" + marginTransactionDto.getOrderId(), "main").getResponseMsg());
        String tradeType = userCommunicationService.sendGet(null, "/orders/tradeType/" + marginTransactionDto.getOrderId(), "main").getResponseMsg();
        System.out.println(orderType);
        System.out.println(tradeType);
        MarginTransaction marginTransaction = MarginTransaction.builder()
                .accountType(marginTransactionDto.getAccountType())
                .dateTime(LocalDateTime.now())
                .orderId(marginTransactionDto.getOrderId())
                .userEmail(email)
                .transactionComment(marginTransactionDto.getTransactionComment())
                .currencyCode("USD")
                .transactionType(marginTransactionDto.getTransactionType())
                .initialMargin(marginTransactionDto.getInitialMargin())
                .loanValue(loanVal)
                .maintenanceMargin(marginTransactionDto.getMaintenanceMargin())
                .interest(orderType.equals("FOREX") ? loanVal * 0.05 : 0.0)
                .build();
        Optional<MarginBalance> marginBalanceFromDb = marginBalanceRepository.findMarginBalanceByListingGroup(ListingGroup.valueOf(orderType));
//        if(tradeType != marginTransactionDto.getTransactionType().toString()){
//            //TODO
//            System.out.println("is this error?");
//        }
        if (marginBalanceFromDb.isPresent()) {
            System.out.println("TU");
            MarginBalance marginBalance = marginBalanceFromDb.get();
            updateBalance(marginBalance, marginTransaction);
            return marginTransactionRepository.save(marginTransaction);
        }
        return null;
    }

    public MarginTransaction findById(String id) {
        Optional<MarginTransaction> marginTransaction = marginTransactionRepository.findById(id);
        if (marginTransaction.isPresent()) {
            return marginTransaction.get();
        }
        return null;
    }

    public List<MarginTransaction> findAll() {
        return marginTransactionRepository.findAll();
    }
//    public MarginTransaction updateMarginTransaction(String id,MarginTransaction marginTransaction){
//        if(marginTransactionRepository.findById(id).isPresent()){
//            return marginTransactionRepository.save(marginTransaction);
//        }
//        return null;
//    }
//
//    public MarginTransaction deleteMarginTransaction(String id){
//        Optional<MarginTransaction> marginTransaction = marginTransactionRepository.findById(id);
//        if(marginTransaction.isEmpty()){
//            marginTransactionRepository.delete(marginTransaction.get());
//            return marginTransaction.get();
//        }else{
//            return null;
//        }
//    }

    public List<MarginTransaction> findMarginsByEmail(String email) {
        return marginTransactionRepository.findMarginTransactionsByUserEmail(email);
    }
}
