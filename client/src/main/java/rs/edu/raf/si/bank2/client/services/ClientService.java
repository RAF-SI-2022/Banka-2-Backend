package rs.edu.raf.si.bank2.client.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<Client> getClient(String id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }



    //todo dodaj hesiranje passowrda
    public Client createClient(ClientDto clientDto) {
        Client newClient = new Client(clientDto.getName(),clientDto.getLastname(),
                clientDto.getDateOfBirth(), clientDto.getGender(),clientDto.getEmail(),clientDto.getTelephone(),
                clientDto.getAddress(),clientDto.getPassword(), new ArrayList<>());
        return clientRepository.save(newClient);
    }

    //TODO ubaciti edit i delete opcije
}
