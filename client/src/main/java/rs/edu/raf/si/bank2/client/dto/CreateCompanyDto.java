package rs.edu.raf.si.bank2.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
