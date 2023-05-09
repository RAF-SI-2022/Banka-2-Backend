package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import rs.edu.raf.si.bank2.main.models.mariadb.Future;
import rs.edu.raf.si.bank2.main.repositories.mariadb.FutureRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.si.bank2.main.services.FutureService;
import rs.edu.raf.si.bank2.main.services.UserService;

@SpringBootTest
public class FutureServiceTest {

    @Mock
    private FutureRepository futureRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FutureService futureService;

    @Test
    void testFindAll() {
        List<Future> futureList = new ArrayList<>();
        Future future1 = new Future();
        future1.setId(1L);
        future1.setFutureName("CLJ22");
        futureList.add(future1);

        Future future2 = new Future();
        future2.setId(2L);
        future2.setFutureName("CLJ23");
        futureList.add(future2);

        when(futureRepository.findAll()).thenReturn(futureList);

        List<Future> result = futureService.findAll();

        assertEquals(2, result.size());
        assertEquals(future1, result.get(0));
        assertEquals(future2, result.get(1));
    }

    @Test
    void testFindById() {
        Future future = new Future();
        future.setId(1L);
        future.setFutureName("CLJ22");

        when(futureRepository.findFutureById(1L)).thenReturn(Optional.of(future));

        Optional<Future> result = futureService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(future, result.get());
    }

    @Test
    void testFindByName() {
        Future future = new Future();
        future.setId(1L);
        future.setFutureName("CLJ22");

        when(futureRepository.findFutureByFutureName("CLJ22")).thenReturn(Optional.of(future));

        Optional<Future> result = futureService.findByName("CLJ22");

        assertEquals(future, result.get());
    }

    @Test
    void testFindByNameWrongName() {
        Future future = new Future();
        future.setId(2L);
        future.setFutureName("CLJ22");

        Future future2 = new Future();
        future2.setId(1L);
        future2.setFutureName("CLJ21");

        when(futureRepository.findFutureByFutureName("CLJ22")).thenReturn(Optional.of(future2));
        when(futureRepository.findFutureByFutureName("CLJ21")).thenReturn(Optional.of(future));

        Optional<Future> result = futureService.findByName("CLJ21");

        assertNotEquals(future2, result.get());
    }

    @Test
    void testBuyFuture() {}

    @Test
    void testSellFuture() {}

    @Test
    void testRemoveFromMarket() {}

    @Test
    void testRemoveWaitingSellFuture() {}

    @Test // ovo vrv ne
    void testWaitingFuturesForUser() {}

    @Test
    void testRegularSell() {}

    @Test
    void testRegularBuy() {}

    @Test
    void testCreateFutureOrder() {}

    @Test
    void testGetTimeStamp() {
        //        LocalDateTime currentDateTime = LocalDateTime.now();
        //        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //
        //        when(futureService)
    }

    @Test
    void testProcessFutureBuyRequest() {}
}
