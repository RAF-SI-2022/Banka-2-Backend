package rs.edu.raf.si.bank2.otc.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PasswordResetRequest {

    private String email;
}
