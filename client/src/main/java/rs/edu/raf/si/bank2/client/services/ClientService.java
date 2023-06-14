package rs.edu.raf.si.bank2.client.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ClientService(ClientRepository clientRepository, JwtUtil jwtUtil) {
        this.clientRepository = clientRepository;
        this.jwtUtil = jwtUtil;
    }

    public Optional<Client> getClient(String id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public String loginUser(String email, String password){
        Optional<Client> client = clientRepository.findClientByEmailAndPassword(email, password);
        if (client.isEmpty()) return null;
        return jwtUtil.generateToken(email);
    }

    public Client save(Client client){
        return clientRepository.save(client);
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
