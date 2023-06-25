package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.otc.models.mongodb.AccountType;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionType;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MarginTransactionDto {
    AccountType accountType; // ENUM - CASH,MARGIN
    Long orderId;
    String transactionComment; // Uplaćivanje sredstava na račun - Initial Margin, Uplaćivanjesredstava na račun
    // -Margin Call, Isplata kamate
    String currencyCode;
    TransactionType transactionType; // ENUM - BUY,SELL, ovo postoji i u orderu tkd NZM
    Double initialMargin;
    Double maintenanceMargin;
}
