package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
//@AllArgsConstructor
@RequiredArgsConstructor
@Document("contactPerson")
public class ContactPerson {

    public ContactPerson(String id, String firstName, String lastName, String phoneNumber, String email, String position, String note) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.note = note;
    }

    @Id
    private String id;
//    @DBRef(lazy = true)
//    @JsonIgnore
//    private Company company;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String position;
    private String note;
}
