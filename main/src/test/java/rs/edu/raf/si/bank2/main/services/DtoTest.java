package rs.edu.raf.si.bank2.main.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.si.bank2.main.dto.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DtoTest {

    @Test
    public void createBalanceDto() {
        BalanceDto balanceDto = new BalanceDto();
        assertNotNull(balanceDto);
    }

    @Test
    public void createBuySellForexDto() {
        BuySellForexDto buySellForexDto = new BuySellForexDto();
        assertNotNull(buySellForexDto);
    }

    @Test
    public void createInflationDto() {
        InflationDto inflationDto = new InflationDto();
        assertNotNull(inflationDto);
    }

    @Test
    public void createMarginTransactionDto() {
        MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
        assertNotNull(marginTransactionDto);
    }

    @Test
    public void createOptionBuyDto() {
        OptionBuyDto optionBuyDto = new OptionBuyDto();
        assertNotNull(optionBuyDto);
    }

    @Test
    public void createOptionSellDto() {
        OptionSellDto optionSellDto = new OptionSellDto();
        assertNotNull(optionSellDto);
    }

    @Test
    public void createPasswordRecoveryDto() {
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        assertNotNull(passwordRecoveryDto);
    }

    @Test
    public void createSellStockUsingOptionDto() {
        SellStockUsingOptionDto sellStockUsingOptionDto = new SellStockUsingOptionDto();
        assertNotNull(sellStockUsingOptionDto);
    }
}
