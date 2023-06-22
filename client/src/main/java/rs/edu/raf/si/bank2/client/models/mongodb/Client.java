package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("client")
public class Client {

    public Client(String name, String lastname, String dateOfBirth, String gender, String email,
                  String telephone, String address, String password, List<Racun> balances) {
        this.name = name;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.telephone = telephone;
        this.address = address;
        this.password = password;
        this.balances = balances;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String lastname;
    private String dateOfBirth;
    private String gender;
    private String email;
    private String telephone;
    private String address;
    private String password;
    private List<Racun> balances;

}
