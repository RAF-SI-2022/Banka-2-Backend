package rs.edu.raf.si.bank2.client.repositories.mongodb;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {

    Optional<Client> findClientByEmail(String email);

    Optional<Client> findClientByEmailAndPassword(String email, String password);
}
