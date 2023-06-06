package rs.edu.raf.si.bank2.otc.dto;

import lombok.Data;

@Data
public class PasswordRecoveryDto {
    private String token;
    private String newPassword;
}
