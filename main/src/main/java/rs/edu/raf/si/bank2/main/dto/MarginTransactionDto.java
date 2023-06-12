package rs.edu.raf.si.bank2.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
