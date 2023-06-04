package rs.edu.raf.si.bank2.otc.models.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("contactPerson")
public class ContactPerson {

    public ContactPerson(Long id, String name, String phoneNumber, String email, String position, String note) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.note = note;
    }

    @Id
    private Long id;
    @DBRef(lazy = true)
    @JsonIgnore
    private Company company;
    private String name;
    private String phoneNumber;
    private String email;
    private String position;
    private String note;
}
