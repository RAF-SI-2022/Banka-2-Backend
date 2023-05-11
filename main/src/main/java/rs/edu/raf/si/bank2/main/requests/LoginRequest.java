package rs.edu.raf.si.bank2.main.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {
    private String email;
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" + "email='" + email + '\'' + ", password='" + password + '\'' + '}';
    }
}
