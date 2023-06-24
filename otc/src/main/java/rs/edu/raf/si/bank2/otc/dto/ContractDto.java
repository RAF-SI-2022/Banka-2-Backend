package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ContractDto {

    private String companyId;
    private ContractElements contractStatus;
    private String contractNumber;
    private String description;
}
