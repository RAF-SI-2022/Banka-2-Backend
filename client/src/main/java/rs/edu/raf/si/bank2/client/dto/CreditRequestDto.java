package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class CreditRequestDto {

    private String clientEmail;
    private Double amount;
    private String usedFor;
    private Double monthlyRate; // mesecno placanje
    private Boolean clientHasJob;
    private String jobLocation;
    private String currentJobDuration;
    private Integer dueDateInMonths;
    private String phoneNumber;

    public CreditRequestDto(
            String clientEmail,
            Double amount,
            String usedFor,
            Double monthlyRate,
            Boolean clientHasJob,
            String jobLocation,
            String currentJobDuration,
            Integer dueDateInMonths,
            String phoneNumber) {
        this.clientEmail = clientEmail;
        this.amount = amount;
        this.usedFor = usedFor;
        this.monthlyRate = monthlyRate;
        this.clientHasJob = clientHasJob;
        this.jobLocation = jobLocation;
        this.currentJobDuration = currentJobDuration;
        this.dueDateInMonths = dueDateInMonths;
        this.phoneNumber = phoneNumber;
    }
}
