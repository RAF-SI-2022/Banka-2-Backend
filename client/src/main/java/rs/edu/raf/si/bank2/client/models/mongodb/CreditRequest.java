package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.CreditApproval;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder
@AllArgsConstructor
//@RequiredArgsConstructor
@Document("creditRequest")
public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String clientEmail;
    private CreditApproval creditApproval; //approved / denied / waiting
    private Double amount;
    private String usedFor;
    private Double monthlyRate;//mesecno placanje
    private Boolean clientHasJob;
    private String jobLocation;
    private String currentJobDuration;
    private Integer dueDateInMonths;
    private String phoneNumber;

    public CreditRequest() {
    }

    public CreditRequest(String clientEmail, CreditApproval creditApproval, Double amount, String usedFor, Double monthlyRate,
                         Boolean clientHasJob, String jobLocation, String currentJobDuration, Integer dueDateInMonths, String phoneNumber) {
        this.clientEmail = clientEmail;
        this.creditApproval = creditApproval;
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
