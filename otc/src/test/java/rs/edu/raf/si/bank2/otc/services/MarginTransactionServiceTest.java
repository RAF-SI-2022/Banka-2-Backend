package rs.edu.raf.si.bank2.otc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.dto.MarginTransactionDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.AccountType;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginBalance;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginTransaction;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionType;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;

@ExtendWith(MockitoExtension.class)
public class MarginTransactionServiceTest {

    @Mock
    private MarginBalanceRepository marginBalanceRepository;

    @Mock
    private UserCommunicationService userCommunicationService;

    @Mock
    private MarginTransactionRepository marginTransactionRepository;

    @InjectMocks
    private MarginTransactionService marginTransactionService;

    //    @Test
    //    void testUpdateBalance_BuyTransaction() {
    //        MarginBalance marginBalance = new MarginBalance();
    //        MarginTransaction marginTransaction = new MarginTransaction();
    //        marginTransaction.setTransactionType(TransactionType.BUY);
    //        marginTransaction.setInitialMargin(100.0);
    //        marginTransaction.setLoanValue(50.0);
    //        marginTransaction.setMaintenanceMargin(10.0);
    //
    //        marginTransactionService.updateBalance(marginBalance, marginTransaction);
    //
    //        assertEquals(100.0, marginBalance.getInvestedResources());
    //        assertEquals(50.0, marginBalance.getLoanedResources());
    //        assertEquals(10.0, marginBalance.getMaintenanceMargin());
    //
    //        verify(marginBalanceRepository).save(marginBalance);
    //    }

    //    @Test
    //    void testUpdateBalance_SellTransaction() {
    //        MarginBalance marginBalance = new MarginBalance();
    //        MarginTransaction marginTransaction = new MarginTransaction();
    //        marginTransaction.setTransactionType(TransactionType.SELL);
    //        marginTransaction.setInitialMargin(100.0);
    //        marginTransaction.setMaintenanceMargin(10.0);
    //
    //        marginTransactionService.updateBalance(marginBalance, marginTransaction);
    //
    //        assertEquals(0.0, marginBalance.getInvestedResources());
    //        assertEquals(0.0, marginBalance.getLoanedResources());
    //        assertEquals(0.0, marginBalance.getMaintenanceMargin());
    //
    //        verify(marginBalanceRepository).save(marginBalance);
    //    }

    @Test
    void testTrim() {
        String input = "{\"responseMsg\":\"LastNumber\"}";
        String expected = "LastNumber";

        String result = marginTransactionService.trim(input);

        assertEquals(expected, result);
    }

    //    @Test
    //    void testMakeTransaction() {
    //        MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
    //        marginTransactionDto.setOrderId(25L);
    //        marginTransactionDto.setInitialMargin(100.0);
    //        marginTransactionDto.setTransactionType(TransactionType.BUY);
    //
    //        String email = "test@example.com";
    //
    //        MarginBalance marginBalance = new MarginBalance();
    //        Optional<MarginBalance> marginBalanceOptional = Optional.of(marginBalance);
    //        when(marginBalanceRepository.findMarginBalanceByListingGroup(any())).thenReturn(marginBalanceOptional);
    //
    //        MarginTransaction marginTransaction = marginTransactionService.makeTransaction(marginTransactionDto,
    // email);
    //
    //        assertEquals(email, marginTransaction.getUserEmail());
    //        assertEquals("USD", marginTransaction.getCurrencyCode());
    //
    //        verify(marginBalanceRepository).findMarginBalanceByListingGroup(any());
    //        verify(marginTransactionRepository).save(marginTransaction);
    //    }

    @Test
    void testFindById_ExistingId() {
        String id = "12345";
        MarginTransaction expectedMarginTransaction = new MarginTransaction();
        when(marginTransactionRepository.findById(id)).thenReturn(Optional.of(expectedMarginTransaction));

        MarginTransaction resultMarginTransaction = marginTransactionService.findById(id);

        assertEquals(expectedMarginTransaction, resultMarginTransaction);

        verify(marginTransactionRepository).findById(id);
    }

    @Test
    void testFindById_NonExistingId() {
        String id = "12345";
        when(marginTransactionRepository.findById(id)).thenReturn(Optional.empty());

        MarginTransaction resultMarginTransaction = marginTransactionService.findById(id);

        assertEquals(null, resultMarginTransaction);

        verify(marginTransactionRepository).findById(id);
    }

    @Test
    void testFindAll() {
        MarginTransaction marginTransaction1 = new MarginTransaction();
        MarginTransaction marginTransaction2 = new MarginTransaction();
        when(marginTransactionRepository.findAll()).thenReturn(List.of(marginTransaction1, marginTransaction2));

        List<MarginTransaction> resultMarginTransactions = marginTransactionService.findAll();

        assertEquals(2, resultMarginTransactions.size());
        assertTrue(resultMarginTransactions.contains(marginTransaction1));
        assertTrue(resultMarginTransactions.contains(marginTransaction2));

        verify(marginTransactionRepository).findAll();
    }

    @Test
    void testFindMarginsByEmail() {
        String email = "test@example.com";
        MarginTransaction marginTransaction1 = new MarginTransaction();
        MarginTransaction marginTransaction2 = new MarginTransaction();
        when(marginTransactionRepository.findMarginTransactionsByUserEmail(email))
                .thenReturn(List.of(marginTransaction1, marginTransaction2));

        List<MarginTransaction> resultMarginTransactions = marginTransactionService.findMarginsByEmail(email);

        assertEquals(2, resultMarginTransactions.size());
        assertTrue(resultMarginTransactions.contains(marginTransaction1));
        assertTrue(resultMarginTransactions.contains(marginTransaction2));

        verify(marginTransactionRepository).findMarginTransactionsByUserEmail(email);
    }

    @Test
    public void testUpdateBalance_BuyTransaction() {
        // Arrange
        //        MockitoAnnotations.openMocks(this);
        MarginBalance marginBalance = new MarginBalance();
        marginBalance.setInvestedResources(100.0);
        marginBalance.setLoanedResources(50.0);
        marginBalance.setMaintenanceMargin(10.0);
        MarginTransaction marginTransaction = new MarginTransaction();
        marginTransaction.setTransactionType(TransactionType.BUY);
        marginTransaction.setInitialMargin(20.0);
        marginTransaction.setLoanValue(30.0);
        marginTransaction.setMaintenanceMargin(5.0);

        // Act
        marginTransactionService.updateBalance(marginBalance, marginTransaction);

        // Assert
        assertEquals(80.0, marginBalance.getInvestedResources());
        assertEquals(80.0, marginBalance.getLoanedResources());
        assertEquals(15.0, marginBalance.getMaintenanceMargin());
        verify(marginBalanceRepository, times(1)).save(marginBalance);
    }

    @Test
    public void testUpdateBalance_SellTransaction() {
        // Arrange
        //        MockitoAnnotations.openMocks(this);
        MarginBalance marginBalance = new MarginBalance();
        marginBalance.setInvestedResources(100.0);
        marginBalance.setLoanedResources(50.0);
        marginBalance.setMaintenanceMargin(10.0);
        MarginTransaction marginTransaction = new MarginTransaction();
        marginTransaction.setTransactionType(TransactionType.SELL);
        marginTransaction.setInitialMargin(20.0);
        marginTransaction.setMaintenanceMargin(5.0);

        // Act
        marginTransactionService.updateBalance(marginBalance, marginTransaction);

        // Assert
        assertEquals(120.0, marginBalance.getInvestedResources());
        assertEquals(5.0, marginBalance.getMaintenanceMargin());
        verify(marginBalanceRepository, times(1)).save(marginBalance);
    }

    @Test
    public void testMakeTransaction() {
        // Arrange
        //        MockitoAnnotations.openMocks(this);
        Long orderId = 60L;
        MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
        marginTransactionDto.setOrderId(orderId);
        marginTransactionDto.setAccountType(AccountType.MARGIN);
        marginTransactionDto.setTransactionComment("comment");
        marginTransactionDto.setTransactionType(TransactionType.BUY);
        marginTransactionDto.setInitialMargin(100.0);
        marginTransactionDto.setMaintenanceMargin(50.0);

        when(userCommunicationService.sendGet(null, "/orders/value/" + orderId, "main"))
                .thenReturn(new CommunicationDto(200, "\"responseMsg\":\"" + "500"));

        when(userCommunicationService.sendGet(null, "/orders/orderType/" + orderId, "main"))
                .thenReturn(new CommunicationDto(200, "\"responseMsg\":\"" + "FOREX"));

        when(userCommunicationService.sendGet(null, "/orders/tradeType/" + orderId, "main"))
                .thenReturn(new CommunicationDto(200, "\"responseMsg\":\"" + "BUY"));

        Optional<MarginBalance> marginBalanceFromDb = Optional.of(MarginBalance.builder()
                .investedResources(100.0)
                .loanedResources(400.0)
                .maintenanceMargin(50.0)
                .build());
        when(marginBalanceRepository.findMarginBalanceByListingGroup(any())).thenReturn(marginBalanceFromDb);

        // Act
        MarginTransaction marginTransaction =
                marginTransactionService.makeTransaction(marginTransactionDto, "user@example.com");

        // Assert
        assertNotNull(marginTransaction);
        assertEquals(AccountType.MARGIN, marginTransaction.getAccountType());
        assertEquals(60L, marginTransaction.getOrderId());
        assertEquals("user@example.com", marginTransaction.getUserEmail());
        assertEquals("comment", marginTransaction.getTransactionComment());
        assertEquals("USD", marginTransaction.getCurrencyCode());
        assertEquals(TransactionType.BUY, marginTransaction.getTransactionType());
        assertEquals(100.0, marginTransaction.getInitialMargin());
        assertEquals(400.0, marginTransaction.getLoanValue());
        assertEquals(50.0, marginTransaction.getMaintenanceMargin());
        assertEquals(20.0, marginTransaction.getInterest());
        assertEquals("FOREX", marginTransaction.getOrderType());

        verify(userCommunicationService, times(4)).sendGet(any(), any(), any());
        verify(marginBalanceRepository, times(1)).findMarginBalanceByListingGroup(any());
        verify(marginTransactionRepository, times(1)).save(marginTransaction);
    }
}
