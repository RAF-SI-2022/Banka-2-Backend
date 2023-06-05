package rs.edu.raf.si.bank2.otc.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.dto.ContractDto;
import rs.edu.raf.si.bank2.otc.dto.OtcResponseDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.*;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.TransactionElementRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OtcService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final ReservedService reservedService;
    private final TransactionElementRepository transactionElementRepository;

    @Autowired
    public OtcService(ContactRepository contactRepository, CompanyRepository companyRepository, ReservedService reservedService, TransactionElementRepository transactionElementRepository) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.reservedService = reservedService;
        this.transactionElementRepository = transactionElementRepository;
    }

    public Optional<Contract> getContract(String id) {
        return contactRepository.findById(id);
    }

    public List<Contract> getAllContracts() {
        return contactRepository.findAll();
    }

    public List<TransactionElement> getAllElements() {
        return transactionElementRepository.findAll();
    }

    public Optional<TransactionElement> getElementById(String id) {
        return transactionElementRepository.findById(id);
    }

    public List<TransactionElement> getElementsForContract(String contractId) {
        Optional<Contract> contract = contactRepository.findById(contractId);

        if (contract.isEmpty()) {
            System.err.println("contract not found");
            return new ArrayList<>();
        }

        return new ArrayList<>(contract.get().getTransactionElements());
    }

    public OtcResponseDto openContract(ContractDto contractDto) {
        Optional<Company> company = companyRepository.findById(contractDto.getCompanyId());

        if (company.isEmpty()) {
            System.err.println("Company not found");
            return new OtcResponseDto(404, "Selektovana kompanija nije u bazi!");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        Contract newContract = new Contract();
        newContract.setContractStatus(contractDto.getContractStatus());
        newContract.setCreationDate(dtf.format(now));
        newContract.setLastUpdatedDate(dtf.format(now));
        newContract.setContractNumber(contractDto.getContractNumber());
        newContract.setDescription(contractDto.getDescription());
        newContract.setTransactionElements(new ArrayList<>());
        contactRepository.save(newContract);

        return new OtcResponseDto(200, "Ugovor je uspesno otvoren");
    }

    public OtcResponseDto editContract(ContractDto updatedContract) {
        Optional<Contract> contract = contactRepository.findById(updatedContract.getCompanyId());

        if (contract.isEmpty()) {
            System.err.println("contract is empty");
            return new OtcResponseDto(404, "Ugovor nije pronadjen u bazi");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        contract.get().setContractStatus(updatedContract.getContractStatus());
        contract.get().setLastUpdatedDate(dtf.format(now));
        contract.get().setDescription(updatedContract.getDescription());
        contactRepository.save(contract.get());

        return new OtcResponseDto(200, "Ugovor je uspesno promenjen");
    }

    public OtcResponseDto addTransactionElementToContract(TransactionElementDto transactionElementDto) {
        Optional<Contract> contract = contactRepository.findById(transactionElementDto.getContractId());

        if (contract.isEmpty()) {
            System.err.println("contract not found");
            return new OtcResponseDto(404, "Selektovani ugovor ne postoji u bazi");
        }
        if (contract.get().getContractStatus() == ContractElements.FINALISED) {
            System.err.println("contract not editable");
            return new OtcResponseDto(500, "Ugovor se ne moze promeniti");
        }

        //kontaktira main service, i skloni resurse (rezervise) tj pemesti ih u transElBazu
        //ili
        //kontaktira main service i rezervise pare za zeljenu hartiju
        CommunicationDto response = reservedService.sendReservation(transactionElementDto);

        //ako je true znaci da je prosla rezervacija, nastavi sa cuvanjem
        if (response.getResponseCode() != 200) return new OtcResponseDto(500, response.getResponseMsg());

        //ako sve prodje napravi element i sacuvaj ga u bazicu
        TransactionElement transactionElement = new TransactionElement();//nemamo contract id jer ih contract sve suva u sebi //todo proveri dal je ok
        transactionElement.setBuyOrSell(transactionElementDto.getBuyOrSell());
        transactionElement.setTransactionElement(transactionElementDto.getTransactionElement());
        transactionElement.setBalance(transactionElementDto.getBalance());
        transactionElement.setCurrency(transactionElementDto.getCurrency());
        transactionElement.setAmount(transactionElementDto.getAmount());
        transactionElement.setPriceOfOneElement(transactionElementDto.getPriceOfOneElement());
        transactionElement.setUserId(transactionElementDto.getUserId());
        transactionElement.setMariaDbId(transactionElementDto.getMariaDbId());
        if (transactionElementDto.getTransactionElement() == TransactionElements.FUTURE) {//sacuvamo future da ga mozemo vratiti ako ubijemo element
            System.err.println(response.getResponseMsg());
            transactionElement.setFutureStorageField(response.getResponseMsg());
        } else transactionElement.setFutureStorageField("");
        transactionElementRepository.save(transactionElement);
        contract.get().getTransactionElements().add(transactionElement);
        contactRepository.save(contract.get());
        return new OtcResponseDto(200, "Element uspesno dodat");
    }

    public OtcResponseDto removeTransactionElement(String contractId, String transactionElementId) {
        Optional<TransactionElement> transactionElement = transactionElementRepository.findById(transactionElementId);
        Optional<Contract> contract = contactRepository.findById(contractId);

        if (transactionElement.isEmpty()) {
            System.err.println("Element not found");
            return new OtcResponseDto(404, "Element ne postoji u bazi");
        }
        if (contract.isEmpty()) {
            System.err.println("Contract not found");
            return new OtcResponseDto(404, "Ugovor ne postoji u bazi");
        }
        //todo skloni stvari sa rezervacije

        CommunicationDto response = reservedService.sendUndoReservation(transactionElement.get());
        if (response.getResponseCode() != 200) return new OtcResponseDto(500, response.getResponseMsg());

        contract.get().getTransactionElements().remove(transactionElement.get());
        contactRepository.save(contract.get());
        transactionElementRepository.delete(transactionElement.get());

        return new OtcResponseDto(200, "Rezervacija uspesno sklonjena");
    }


    public OtcResponseDto deleteContract(String id) {
        Optional<Contract> contract = contactRepository.findById(id);

        if (contract.isEmpty()) {
            System.err.println("Ugovor nije u bazi");
            return new OtcResponseDto(404, "Ugovor nije u bazi");
        }

        for (TransactionElement te : contract.get().getTransactionElements()){//sklonimo sve rezervacije koje imamo
            this.removeTransactionElement(contract.get().getId(), te.getId());
        }

        contactRepository.deleteById(id);
        return new OtcResponseDto(200, "Ugovor uspesno izbrisan");
    }

    public OtcResponseDto closeContract(String id) {
        Optional<Contract> contract = contactRepository.findById(id);

        if (contract.isEmpty()) {
            System.err.println("Ugovor nije u bazi");
            return new OtcResponseDto(404, "Ugovor nije u bazi");
        }

        contract.get().setContractStatus(ContractElements.FINALISED);
        contactRepository.save(contract.get());

        //todo obradi sve sto treba

        return new OtcResponseDto(200, "Ugovor uspesno kompletiran");
    }


}
