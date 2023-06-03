package rs.edu.raf.si.bank2.otc.services;


import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.services.interfaces.OtcServiceInterface;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OtcService implements OtcServiceInterface {

    private final ContactRepository contactRepository;

    @Autowired
    public OtcService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Optional<Contract> getContract(String id) {
        return contactRepository.findById(id);
    }

    @Override
    public List<Contract> getAllContracts() {
        return contactRepository.findAll();
    }

    @Override
    public Contract editContract(String id, Contract updatedContract) {
        Optional<Contract> contract = contactRepository.findById(id);

        if (contract.isEmpty()) {
            System.err.println("contract is empty");//todo check
        }

        contract.get().setContactStatus(updatedContract.getContactStatus());
//        contract.get().setLastUpdatedDate();
        contract.get().setContactStatus(updatedContract.getContactStatus());
        contract.get().setContactStatus(updatedContract.getContactStatus());

        return null;
    }

    @Override
    public Contract addTransactionElement(String id, TransactionElement transactionElement) {
        return null;
    }

    @Override
    public Contract removeTransactionElement(String id, TransactionElement transactionElement) {
        return null;
    }

    @Override
    public Contract editTransactionElement(String id, TransactionElement transactionElement) {
        return null;
    }

    @Override
    public void deleteContract(String id) {
        Optional<Contract> contract = contactRepository.findById(id);

        if (contract.isEmpty()){//todo baci exception neki ili nesto
            System.err.println("nije nadjen");
        }

        contactRepository.deleteById(id);

    }


}
