package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ContactPersonDto {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String position;
    private String note;
}
