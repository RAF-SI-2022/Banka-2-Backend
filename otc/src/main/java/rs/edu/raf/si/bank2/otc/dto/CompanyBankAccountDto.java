package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CompanyBankAccountDto {
    private Long id;
    private String accountNumber;
    private String bankName;
    private String accountType;
}
