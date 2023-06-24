package rs.edu.raf.si.bank2.main.services;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.exceptions.ExchangeNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.Exchange;
import rs.edu.raf.si.bank2.main.repositories.mariadb.ExchangeRepository;
import rs.edu.raf.si.bank2.main.services.interfaces.ExchangeServiceInterface;

@EnableCaching
@Service
public class ExchangeService implements ExchangeServiceInterface {

    private ExchangeRepository exchangeRepository;
    private UserService userService;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository, UserService userService) {
        this.exchangeRepository = exchangeRepository;
        this.userService = userService;
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Cacheable(value = "exchangesALL")
    @Override
    public List<Exchange> findAll() {
        //        System.out.println(redisTemplate.keys("*"));
        //      za test ovo moze da se otkomentarise i tacno se vidi da kesira kljuceve redis :)
        System.out.println("Getting all exchanges first time (caching into redis)");
        return exchangeRepository.findAll();
    }

    @Override
    @Cacheable(value = "exchangesID", key = "#id")
    public Exchange findById(Long id) {
        //        System.out.println(redisTemplate.keys("*"));
        //      za test ovo moze da se otkomentarise i tacno se vidi da kesira kljuceve redis :)

        System.out.println("Getting exchange by id first time (caching into redis)");

        Optional<Exchange> exchange = exchangeRepository.findById(id);
        if (exchange.isPresent()) {
            return exchange.get();
        } else {
            throw new ExchangeNotFoundException();
        }
    }

    @Override
    @Cacheable(value = "exchangesMIC", key = "#micCode")
    public Exchange findByMicCode(String micCode) {
        //        System.out.println(redisTemplate.keys("*"));
        //      za test ovo moze da se otkomentarise i tacno se vidi da kesira kljuceve redis :)

        System.out.println("Getting exchange for micCode first time (caching into redis)");
        Optional<Exchange> exchange = exchangeRepository.findExchangeByMicCode(micCode);
        if (exchange.isPresent()) {
            return exchange.get();
        } else {
            throw new ExchangeNotFoundException();
        }
    }

    @Override
    @Cacheable(value = "exchangesAcronym", key = "#acronym")
    public Exchange findByAcronym(String acronym) {
        //        System.out.println(redisTemplate.keys("*"));
        //      za test ovo moze da se otkomentarise i tacno se vidi da kesira kljuceve redis :)

        System.out.println("Getting exchange for acronym first time (caching into redis)");
        Optional<Exchange> exchange = exchangeRepository.findExchangeByAcronym(acronym);
        if (exchange.isPresent()) {
            return exchange.get();
        } else {
            throw new ExchangeNotFoundException();
        }
    }

    public boolean isExchangeActive(String micCode) {
        Optional<Exchange> exchangeEntry = exchangeRepository.findExchangeByMicCode(micCode);
        if (exchangeEntry.isEmpty()) {
            throw new ExchangeNotFoundException();
        }
        Exchange exchange = exchangeEntry.get(); // todo nisu radile vikendom pa je sklonjeno vrati posle
        LocalTime openTime = LocalTime.parse(exchange.getOpenTime().substring(1));
        LocalTime closeTime = LocalTime.parse(exchange.getCloseTime().substring(1));
        ZoneId zone = ZoneId.of(exchange.getTimeZone());
        LocalTime currTime = LocalTime.now(zone);
        return !currTime.isBefore(openTime) && !currTime.isAfter(closeTime);
        //        return true;
    }
}
