package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.models.mongodb.Credit;
import rs.edu.raf.si.bank2.client.repositories.mongodb.CreditRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CreditService {

    private final CreditRepository creditRepository;

    @Autowired
    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }



    public List<Credit> getAll(){

        return null;
    }

    public List<Credit> getAllForClient(){
        return null;
    }

    public Credit save(){
        return null;
    }

    public Credit requestCredit(){

        return null;
    }


    public Credit approveOrDenyCredit(String creditId){

//        Optional<Credit> credit = creditRepository.findById(creditId);
//
//        if (credit.isEmpty())
//
        return null;

    }



}
