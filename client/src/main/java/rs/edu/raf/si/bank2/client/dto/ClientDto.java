package rs.edu.raf.si.bank2.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ClientDto {
    private String name;
    private String lastname;
    private String dateOfBirth;
    private String gender;
    private String email;
    private String telephone;
    private String address;
    private String password;
}
