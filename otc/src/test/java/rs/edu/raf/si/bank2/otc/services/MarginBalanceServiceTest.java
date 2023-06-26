package rs.edu.raf.si.bank2.otc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.otc.models.mongodb.AccountType;
import rs.edu.raf.si.bank2.otc.models.mongodb.ListingGroup;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginBalance;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;

@ExtendWith(MockitoExtension.class)
public class MarginBalanceServiceTest {
    @Mock
    private MarginBalanceRepository marginBalanceRepository;

    @Mock
    private MarginTransactionRepository marginTransactionRepository;

    @InjectMocks
    private MarginBalanceService marginBalanceService;

    @Test
    void testGetAllMarginBalances() {
        // Arrange
        List<MarginBalance> expectedMarginBalances = Arrays.asList(
                MarginBalance.builder()
                        .id("test1")
                        .accountType(AccountType.MARGIN)
                        .currencyCode("USD")
                        .listingGroup(ListingGroup.FOREX)
                        .investedResources(1000.0)
                        .loanedResources(500.0)
                        .maintenanceMargin(200.0)
                        .marginCall(false)
                        .build(),
                MarginBalance.builder()
                        .id("test2")
                        .accountType(AccountType.MARGIN)
                        .currencyCode("EUR")
                        .listingGroup(ListingGroup.STOCK)
                        .investedResources(2000.0)
                        .loanedResources(1000.0)
                        .maintenanceMargin(300.0)
                        .marginCall(true)
                        .build());
        when(marginBalanceRepository.findAll()).thenReturn(expectedMarginBalances);

        // Act
        List<MarginBalance> actualMarginBalances = marginBalanceService.getAllMarginBalances();

        // Assert
        assertEquals(expectedMarginBalances.size(), actualMarginBalances.size());
        assertEquals(expectedMarginBalances, actualMarginBalances);
        verify(marginBalanceRepository, times(1)).findAll();
    }

    @Test
    void testGetMarginBalanceById() {
        // Arrange
        String marginBalanceId = "1";
        MarginBalance expectedMarginBalance = MarginBalance.builder()
                .id("test1")
                .accountType(AccountType.MARGIN)
                .currencyCode("USD")
                .listingGroup(ListingGroup.STOCK)
                .investedResources(2000.0)
                .loanedResources(800.0)
                .maintenanceMargin(600.0)
                .marginCall(false)
                .build();
        when(marginBalanceRepository.findById(marginBalanceId)).thenReturn(Optional.of(expectedMarginBalance));

        // Act
        MarginBalance actualMarginBalance = marginBalanceService.getMarginBalanceById(marginBalanceId);

        // Assert
        assertNotNull(actualMarginBalance);
        assertEquals(expectedMarginBalance, actualMarginBalance);
        verify(marginBalanceRepository, times(1)).findById(marginBalanceId);
    }

    @Test
    void testGetMarginBalanceById_NonexistentId() {
        // Arrange
        String marginBalanceId = "nonexistent";
        when(marginBalanceRepository.findById(marginBalanceId)).thenReturn(Optional.empty());

        // Act
        MarginBalance actualMarginBalance = marginBalanceService.getMarginBalanceById(marginBalanceId);

        // Assert
        assertNull(actualMarginBalance);
        verify(marginBalanceRepository, times(1)).findById(marginBalanceId);
    }

    @Test
    void testCreateMarginBalance() {
        // Arrange
        MarginBalance marginBalanceToCreate = MarginBalance.builder()
                .id("test1")
                .accountType(AccountType.MARGIN)
                .currencyCode("USD")
                .listingGroup(ListingGroup.OPTIONS)
                .investedResources(2000.0)
                .loanedResources(800.0)
                .maintenanceMargin(600.0)
                .marginCall(false)
                .build();
        when(marginBalanceRepository.save(marginBalanceToCreate)).thenReturn(marginBalanceToCreate);

        // Act
        MarginBalance createdMarginBalance = marginBalanceService.createMarginBalance(marginBalanceToCreate);

        // Assert
        assertNotNull(createdMarginBalance);
        assertEquals(marginBalanceToCreate, createdMarginBalance);
        verify(marginBalanceRepository, times(1)).save(marginBalanceToCreate);
    }

    @Test
    void testUpdateMarginBalance() {
        // Arrange
        MarginBalance marginBalanceToUpdate = MarginBalance.builder()
                .id("test1")
                .accountType(AccountType.MARGIN)
                .currencyCode("USD")
                .listingGroup(ListingGroup.STOCK)
                .investedResources(2000.0)
                .loanedResources(800.0)
                .maintenanceMargin(600.0)
                .marginCall(false)
                .build();
        when(marginBalanceRepository.save(marginBalanceToUpdate)).thenReturn(marginBalanceToUpdate);

        // Act
        MarginBalance updatedMarginBalance = marginBalanceService.updateMarginBalance(marginBalanceToUpdate);

        // Assert
        assertNotNull(updatedMarginBalance);
        assertEquals(marginBalanceToUpdate, updatedMarginBalance);
        verify(marginBalanceRepository, times(1)).save(marginBalanceToUpdate);
    }

    @Test
    void testDeleteMarginBalance() {
        // Arrange
        MarginBalance marginBalanceToDelete = MarginBalance.builder()
                .id("test1")
                .accountType(AccountType.MARGIN)
                .currencyCode("USD")
                .listingGroup(ListingGroup.FOREX)
                .investedResources(2000.0)
                .loanedResources(800.0)
                .maintenanceMargin(600.0)
                .marginCall(false)
                .build();

        // Act
        marginBalanceService.deleteMarginBalance(marginBalanceToDelete);

        // Assert
        verify(marginBalanceRepository, times(1)).delete(marginBalanceToDelete);
    }
}
