package rs.edu.raf.si.bank2.otc.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.dto.ContractDto;
import rs.edu.raf.si.bank2.otc.dto.OtcResponseDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.*;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.TransactionElementRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OtcServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ReservedService reservedService;

    @Mock
    private TransactionElementRepository transactionElementRepository;

    @InjectMocks
    private OtcService otcService;

    @Test
    void getContract_ShouldReturnContract_WhenValidIdProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "123";
        Contract expectedContract = new Contract();
        when(contactRepository.findById(contractId)).thenReturn(Optional.of(expectedContract));

        Optional<Contract> result = otcService.getContract(contractId);

        assertEquals(Optional.of(expectedContract), result);
    }

    @Test
    void getAllContracts_ShouldReturnAllContracts() {
        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        List<Contract> expectedContracts = new ArrayList<>();
        when(contactRepository.findAll()).thenReturn(expectedContracts);

        List<Contract> result = otcService.getAllContracts();

        assertEquals(expectedContracts, result);
    }

    @Test
    void getAllContractsForUserId_ShouldReturnAllContractsForUserId() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        Long userId = 123L;
        List<Contract> expectedContracts = new ArrayList<>();
        when(contactRepository.findByUserId(userId)).thenReturn(expectedContracts);

        List<Contract> result = otcService.getAllContractsForUserId(userId);

        assertEquals(expectedContracts, result);
    }

    @Test
    void getAllElements_ShouldReturnAllElements() {
        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        List<TransactionElement> expectedElements = new ArrayList<>();
        when(transactionElementRepository.findAll()).thenReturn(expectedElements);

        List<TransactionElement> result = otcService.getAllElements();

        assertEquals(expectedElements, result);
    }

    @Test
    void getElementById_ShouldReturnElement_WhenValidIdProvided() {
        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String elementId = "456";
        TransactionElement expectedElement = new TransactionElement();
        when(transactionElementRepository.findById(elementId)).thenReturn(Optional.of(expectedElement));

        Optional<TransactionElement> result = otcService.getElementById(elementId);

        assertEquals(Optional.of(expectedElement), result);
    }

    @Test
    void getElementsForContract_ShouldReturnElementsForContract_WhenValidContractIdProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "789";
        Contract contract = new Contract();
        contract.setTransactionElements(new ArrayList<>());
        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));

        List<TransactionElement> result = otcService.getElementsForContract(contractId);

        assertEquals(contract.getTransactionElements(), result);
    }

    @Test
    void getElementsForContract_ShouldReturnEmptyArrayList_WhenInvalidContractIdProvided() {

        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        List<TransactionElement> result = otcService.getElementsForContract(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void openContract_ShouldReturnOtcResponseDto_WhenValidUserIdAndContractDtoProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        Long userId = 123L;
        ContractDto contractDto = new ContractDto();
        Company company = new Company();
        company.setId("1");
        when(companyRepository.findById(contractDto.getCompanyId())).thenReturn(Optional.of(company));

        OtcResponseDto result = otcService.openContract(userId, contractDto);

        assertEquals(new OtcResponseDto(200, "Ugovor je uspesno otvoren"), result);
        verify(contactRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void openContract_ShouldReturnOtcResponseDtoWithErrorMessage_WhenInvalidCompanyIdProvided() {

        ContractDto contractDto = ContractDto.builder().companyId("1").build();

        when(companyRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto result = otcService.openContract(1L, contractDto);

        assertEquals(404, result.getResponseCode());
        assertEquals("Selektovana kompanija nije u bazi!", result.getResponseMsg());
    }


    @Test
    void editContract_ShouldReturnOtcResponseDto_WhenValidContractDtoProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        ContractDto updatedContract = new ContractDto();
        updatedContract.setCompanyId("123");
        Contract contract = new Contract();
        when(contactRepository.findById(updatedContract.getCompanyId())).thenReturn(Optional.of(contract));

        OtcResponseDto result = otcService.editContract(updatedContract);

        assertEquals(new OtcResponseDto(200, "Ugovor je uspesno promenjen"), result);
        verify(contactRepository, times(1)).save(eq(contract));
    }

    @Test
    void editContract_ShouldReturnOtcResponseDtoWithErrorMessage_WhenInvalidCompanyIdProvided() {
        ContractDto contractDto = ContractDto.builder().companyId("1").build();

        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto result = otcService.editContract(contractDto);

        assertEquals(404, result.getResponseCode());
        assertEquals("Ugovor nije pronadjen u bazi", result.getResponseMsg());
    }


    @Test
    void addTransactionElementToContract_ShouldReturnOtcResponseDto_WhenValidTransactionElementDtoProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        TransactionElementDto transactionElementDto = new TransactionElementDto();
        Contract contract = Contract.builder().transactionElements(new ArrayList<>()).build();
        contract.setId("123");
        when(contactRepository.findById(transactionElementDto.getContractId())).thenReturn(Optional.of(contract));
        when(reservedService.sendReservation(transactionElementDto)).thenReturn(new CommunicationDto(200, "Success"));

        OtcResponseDto result = otcService.addTransactionElementToContract(transactionElementDto);

        assertEquals(new OtcResponseDto(200, "Element uspesno dodat"), result);
        verify(transactionElementRepository, times(1)).save(any(TransactionElement.class));
        verify(contactRepository, times(1)).save(eq(contract));
    }

    @Test
    void addTransactionElementToContract_ShouldReturnOtcResponseDtoWithErrorMessage_WhenInvalidContractIdProvided() {

        TransactionElementDto transactionElementDto = new TransactionElementDto();

        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto otcResponseDto = otcService.addTransactionElementToContract(transactionElementDto);

        assertEquals(404, otcResponseDto.getResponseCode());
        assertEquals("Selektovani ugovor ne postoji u bazi", otcResponseDto.getResponseMsg());
    }

    @Test
    void addTransactionElementToContract_ShouldReturnOtcResponseDtoWithErrorMessage_WhenContractStatusIsFinalised() {

        Contract contract = Contract.builder().contractStatus(ContractElements.FINALISED).build();

        TransactionElementDto transactionElementDto = new TransactionElementDto();

        when(contactRepository.findById(any())).thenReturn(Optional.ofNullable(contract));

        OtcResponseDto otcResponseDto = otcService.addTransactionElementToContract(transactionElementDto);

        assertEquals(500, otcResponseDto.getResponseCode());
        assertEquals("Ugovor se ne moze promeniti", otcResponseDto.getResponseMsg());
    }

    @Test
    void addTransactionElementToContract_ShouldReturnOtcResponseDtoWithErrorMessage_WhenResponseCodeIsNot200 () {

        Contract contract = Contract.builder().contractStatus(ContractElements.BUY).build();

        TransactionElementDto transactionElementDto = new TransactionElementDto();

        when(contactRepository.findById(any())).thenReturn(Optional.ofNullable(contract));

        when(reservedService.sendReservation(transactionElementDto)).thenReturn(new CommunicationDto(201, "Success"));

        OtcResponseDto otcResponseDto = otcService.addTransactionElementToContract(transactionElementDto);

        assertEquals(500, otcResponseDto.getResponseCode());
    }

    @ParameterizedTest
    @MethodSource("addTransactionElementToContract")
    void addTransactionElementToContract_ShouldReturnOtcResponseDto_success(ContractElements contractElement, TransactionElements transactionElements) {

        Contract contract = Contract.builder().transactionElements(new ArrayList<>()).contractStatus(contractElement).build();

        TransactionElementDto transactionElementDto = TransactionElementDto.builder().buyOrSell(contractElement).transactionElement(transactionElements).build();

        when(contactRepository.findById(any())).thenReturn(Optional.ofNullable(contract));

        when(reservedService.sendReservation(transactionElementDto)).thenReturn(new CommunicationDto(200, "Success"));

        OtcResponseDto result = otcService.addTransactionElementToContract(transactionElementDto);

        assertEquals(new OtcResponseDto(200, "Element uspesno dodat"), result);
        verify(transactionElementRepository, times(1)).save(any(TransactionElement.class));
    }

    static Stream<Arguments> addTransactionElementToContract() {
        return Stream.of(
                Arguments.of(ContractElements.BUY, TransactionElements.FUTURE),
                Arguments.of(ContractElements.BUY, TransactionElements.STOCK),
                Arguments.of(ContractElements.SELL, TransactionElements.FUTURE),
                Arguments.of(ContractElements.SELL, TransactionElements.STOCK)
        );
    }

    @Test
    void removeTransactionElement_ShouldReturnOtcResponseDto_WhenValidContractIdAndTransactionElementIdProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "123";
        String transactionElementId = "456";
        TransactionElement transactionElement = new TransactionElement();
        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setTransactionElements(new ArrayList<>());
        contract.getTransactionElements().add(transactionElement);
        when(transactionElementRepository.findById(transactionElementId)).thenReturn(Optional.of(transactionElement));
        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(reservedService.sendUndoReservation(transactionElement)).thenReturn(new CommunicationDto(200, "Success"));

        OtcResponseDto result = otcService.removeTransactionElement(contractId, transactionElementId);

        assertEquals(new OtcResponseDto(200, "Rezervacija uspesno sklonjena"), result);
        verify(transactionElementRepository, times(1)).delete(eq(transactionElement));
        verify(contactRepository, times(1)).save(eq(contract));
    }

    @Test
    void removeTransactionElement_ShouldReturnOtcResponseDtoWithErrorMessage_WhenInvalidTransactionElementIdProvided() {

        when(transactionElementRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto otcResponseDto = otcService.removeTransactionElement("1", "1");

        assertEquals(404, otcResponseDto.getResponseCode());
        assertEquals("Element ne postoji u bazi", otcResponseDto.getResponseMsg());
    }

    @Test
    void removeTransactionElement_ShouldReturnOtcResponseDtoWithErrorMessage_WhenInvalidContractIdProvided() {

        TransactionElement transactionElement = new TransactionElement();

        when(transactionElementRepository.findById(any())).thenReturn(Optional.of(transactionElement));
        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto otcResponseDto = otcService.removeTransactionElement("1", "1");

        assertEquals(404, otcResponseDto.getResponseCode());
        assertEquals("Ugovor ne postoji u bazi", otcResponseDto.getResponseMsg());
    }

    @Test
    void removeTransactionElement_ShouldReturnOtcResponseDtoWithErrorMessage_WhenResponseCodeIsNot200() {

        TransactionElement transactionElement = new TransactionElement();
        Contract contract = new Contract();

        when(transactionElementRepository.findById(any())).thenReturn(Optional.of(transactionElement));
        when(contactRepository.findById(any())).thenReturn(Optional.of(contract));

        when(reservedService.sendUndoReservation(transactionElement)).thenReturn(new CommunicationDto(201, "Success"));

        OtcResponseDto otcResponseDto = otcService.removeTransactionElement("1", "1");

        assertEquals(500, otcResponseDto.getResponseCode());
    }

    @Test
    void deleteContract_ShouldReturnOtcResponseDto_WhenValidContractIdProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "123";
        Contract contract = Contract.builder().transactionElements(new ArrayList<>()).build();
        contract.setId(contractId);
        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));

        OtcResponseDto result = otcService.deleteContract(contractId);

        assertEquals(new OtcResponseDto(200, "Ugovor uspesno izbrisan"), result);
        verify(contactRepository, times(1)).deleteById(eq(contractId));
    }

    @Test
    void deleteContract_ShouldReturnOtcResponseDtoWithErrorMessage_WhenInvalidContractIdProvided() {

        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto otcResponseDto = otcService.deleteContract("1");

        assertEquals(404, otcResponseDto.getResponseCode());
        assertEquals("Ugovor nije u bazi", otcResponseDto.getResponseMsg());
    }

    @Test
    void deleteContract_ShouldReturnOtcResponseDto_WhenRemovingTransactionElements() {
        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "123";
        TransactionElement te1 = TransactionElement.builder().id("1").build();
        TransactionElement te2 = TransactionElement.builder().id("2").build();

        List<TransactionElement> transactionElementList = new ArrayList<>();
        transactionElementList.add(te1);
        transactionElementList.add(te2);

        Contract contract = Contract.builder().transactionElements(transactionElementList).id(contractId).build();
        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(transactionElementRepository.findById(te1.getId())).thenReturn(Optional.of(te1));
        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(reservedService.sendUndoReservation(te1)).thenReturn(new CommunicationDto(200, "Success"));

        otcService.deleteContract(contractId);
    }

    @Test
    void closeContract_ShouldReturnOtcResponseDto_WhenValidContractIdProvided() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "123";

        List<TransactionElement> transactionElements = new ArrayList<>();
        TransactionElement te1 = new TransactionElement();
        TransactionElement te2 = new TransactionElement();
        transactionElements.add(te1);
        transactionElements.add(te2);

        Contract contract = Contract.builder().transactionElements(transactionElements).build();
        contract.setId(contractId);

        CommunicationDto communicationDto = new CommunicationDto(200, any());

        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(reservedService.finalizeElement(te1)).thenReturn(communicationDto);

        OtcResponseDto result = otcService.closeContract(contractId);

        assertEquals(new OtcResponseDto(200, "Ugovor uspesno kompletiran"), result);
        verify(contactRepository, times(1)).save(eq(contract));
        assertEquals(ContractElements.FINALISED, contract.getContractStatus());
    }

    @Test
    void closeContract_ShouldReturnOtcResponseDto_WhenInvalidStatusIsReturnedInCommunicationDto() {

        OtcService otcService = new OtcService(contactRepository, companyRepository, reservedService, transactionElementRepository);
        String contractId = "123";

        List<TransactionElement> transactionElements = new ArrayList<>();
        TransactionElement te1 = new TransactionElement();
        TransactionElement te2 = new TransactionElement();
        transactionElements.add(te1);
        transactionElements.add(te2);

        Contract contract = Contract.builder().transactionElements(transactionElements).build();
        contract.setId(contractId);

        CommunicationDto communicationDto = new CommunicationDto(201, any());

        when(contactRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(reservedService.finalizeElement(te1)).thenReturn(communicationDto);

        OtcResponseDto result = otcService.closeContract(contractId);

        assertEquals(201, communicationDto.getResponseCode());
    }

    @Test
    void closeContract_ShouldReturnOtcResponseDto_WhenInvalidContractIdProvided() {

        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        OtcResponseDto otcResponseDto = otcService.closeContract("1");

        assertEquals(404, otcResponseDto.getResponseCode());
        assertEquals("Ugovor nije u bazi", otcResponseDto.getResponseMsg());
    }
}
