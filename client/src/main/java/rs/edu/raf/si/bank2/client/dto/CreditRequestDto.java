package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class CreditRequestDto {

    private String clientEmail;
    private Double amount;
    private String usedFor;
    private Double monthlyRate;//mesecno placanje
    private Boolean clientHasJob;
    private String jobLocation;
    private String currentJobDuration;
    private Integer dueDateInMonths;
    private String phoneNumber;

}
