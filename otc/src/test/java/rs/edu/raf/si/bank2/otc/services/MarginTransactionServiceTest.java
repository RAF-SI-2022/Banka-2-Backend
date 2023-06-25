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
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginTransaction;
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
}
