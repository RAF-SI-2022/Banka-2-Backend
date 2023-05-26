package rs.edu.raf.si.bank2.users.services.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import rs.edu.raf.si.bank2.users.models.mariadb.Future;
import rs.edu.raf.si.bank2.users.requests.FutureRequestBuySell;

public interface FutureServiceInterface {

    List<Future> findAll();

    Optional<Future> findById(Long Id);

    Optional<List<Future>> findFuturesByFutureName(String futureName);

    ResponseEntity<?> buyFuture(FutureRequestBuySell futureRequest, String fromUserEmail, Float usersMoneyInCurrency);

    ResponseEntity<?> sellFuture(FutureRequestBuySell futureRequest);

    ResponseEntity<?> removeFromMarket(Long futureId);

    List<Long> getWaitingFuturesForUser(Long userId, String type, String futureName);

    Optional<List<Future>> findFuturesByUserId(Long id);

    @Deprecated
    Optional<Future> findByName(String contractName);

    ResponseEntity<?> removeWaitingSellFuture(Long id);

    ResponseEntity<?> removeWaitingBuyFuture(Long id);
}
