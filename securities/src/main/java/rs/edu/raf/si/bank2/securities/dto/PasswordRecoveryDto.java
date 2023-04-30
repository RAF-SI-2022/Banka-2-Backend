package rs.edu.raf.si.bank2.securities.dto;

import lombok.Data;

@Data
public class PasswordRecoveryDto {
    private String token;
    private String newPassword;
}
