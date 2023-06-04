package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateCompanyDto {

    private String name;
    private String registrationNumber;
    private String taxNumber;
    private String activityCode;
    private String address;
}
