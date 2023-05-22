package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.controllers.FutureController;
import rs.edu.raf.si.bank2.main.models.mariadb.Future;
import rs.edu.raf.si.bank2.main.repositories.mariadb.FutureRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FutureServiceTest {

    @Mock
    private FutureRepository futureRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FutureService futureService;

    @InjectMocks
    private FutureController futureController;

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

        when(futureRepository.findFutureByFutureName("CLJ21")).thenReturn(Optional.of(future));

        Optional<Future> result = futureService.findByName("CLJ21");

        assertNotEquals(future2, result.get());
    }

    /*@Test
    void testBuyFuture() {
        Future future = new Future();
        future.setId(2L);
        future.setFutureName("CLJ22");

        User user = User.builder()
                .id(1L)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .permissions(Collections.singletonList(Permission.builder()
                        .permissionName(PermissionName.ADMIN_USER)
                        .build()))
                .build();


        FutureRequestBuySell futureRequest = new FutureRequestBuySell();
        futureRequest.setFutureName("test");
        futureRequest.setId(3L);
        futureRequest.setPrice(1);
        futureRequest.setLimit(2);
        futureRequest.setAction("BUY");
        futureRequest.setStop(2);
        futureRequest.setUserId(1L);
        futureRequest.setCurrencyCode("EUR");

        futureController.buyFuture(futureRequest);
    }*/

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
