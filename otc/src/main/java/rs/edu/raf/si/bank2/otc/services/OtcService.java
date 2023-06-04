package rs.edu.raf.si.bank2.otc.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.ContractDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.TransactionElementRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OtcService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final TransactionElementRepository transactionElementRepository;

    @Autowired
    public OtcService(ContactRepository contactRepository, CompanyRepository companyRepository, TransactionElementRepository transactionElementRepository) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.transactionElementRepository = transactionElementRepository;
    }

    public Optional<Contract> getContract(String id) {
        return contactRepository.findById(id);
    }

    public List<Contract> getAllContracts() {
        return contactRepository.findAll();
    }

    public String openContract(ContractDto contractDto){
        Optional<Company> company = companyRepository.findById(contractDto.getCompanyId());

        if (company.isEmpty()){
            System.err.println("Company not found");
            return "Selektovana kompanija nije u bazi!";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        Contract newContract = new Contract();
        newContract.setContactStatus(contractDto.getContractStatus());
        newContract.setCreationDate(dtf.format(now));
        newContract.setLastUpdatedDate(dtf.format(now));
        newContract.setContractNumber(contractDto.getContractNumber());
        newContract.setDescription(contractDto.getDescription());
        newContract.setNote(contractDto.getNote());
        contactRepository.save(newContract);

        return "Ugovor je uspesno otvoren";
    }

    public String editContract(String id, ContractDto updatedContract) {
        Optional<Contract> contract = contactRepository.findById(id);

        if (contract.isEmpty()) {
            System.err.println("contract is empty");
            return "Ugovor nije pronadjen u bazi";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        contract.get().setContactStatus(updatedContract.getContractStatus());
        contract.get().setLastUpdatedDate(dtf.format(now));
        contract.get().setDescription(updatedContract.getDescription());
        contract.get().setNote(updatedContract.getNote());
        contactRepository.save(contract.get());

        return "Ugovor je uspesno promenjen";
    }

    public String addTransactionElementToContract(TransactionElementDto transactionElementDto) {
        Optional<Contract> contract = contactRepository.findById(transactionElementDto.getContractId());

        if (contract.isEmpty()) {
            System.err.println("contract not found");
            return "Selektovani ugovor ne postoji u bazi";
        }

        TransactionElement transactionElement = new TransactionElement();

        transactionElementRepository.save(transactionElement);
        contract.get().getTransactionElements().add(transactionElement);
        contactRepository.save(contract.get());
        return "Element uspesno dodat";
    }

    public Contract removeTransactionElement(String contractId, String transactionElementId) {
        Optional<TransactionElement> transactionElement = transactionElementRepository.findById(transactionElementId);
        Optional<Contract> contract = contactRepository.findById(contractId);

        if (transactionElement.isEmpty()) System.err.println("element not found");//todo dodja check
        if (contract.isEmpty()) System.err.println("element not found");//todo dodja check

        contract.get().getTransactionElements().remove(transactionElement.get());
        contactRepository.save(contract.get());
        transactionElementRepository.deleteById(transactionElementId);

        return contract.get();
    }

    public String editTransactionElement(String transactionElementId, TransactionElement updatedTElement) {
        Optional<TransactionElement> transactionElement = transactionElementRepository.findById(transactionElementId);

        if (transactionElement.isEmpty()) {
            System.err.println("element not found");
            return "Element nije pronadjen u bazi";
        }
        transactionElement.get().setBalance(updatedTElement.getBalance());
        transactionElement.get().setCurrency(updatedTElement.getCurrency());
        transactionElement.get().setAmount(updatedTElement.getAmount());
        transactionElement.get().setPriceOfOneElement(updatedTElement.getPriceOfOneElement());

        return "Element je uspesno izmenjen";
    }

    public void deleteContract(String id) {
        Optional<Contract> contract = contactRepository.findById(id);

        if (contract.isEmpty()){//todo baci exception neki ili nesto
            System.err.println("nije nadjen");
        }

        contactRepository.deleteById(id);

    }


}
