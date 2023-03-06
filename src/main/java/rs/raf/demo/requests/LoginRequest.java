package rs.raf.demo.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String mail;
    private String password;
}
