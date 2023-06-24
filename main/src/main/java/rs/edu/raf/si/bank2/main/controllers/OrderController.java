package rs.edu.raf.si.bank2.main.controllers;

import io.micrometer.core.annotation.Timed;
import java.text.ParseException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.dto.CommunicationDto;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.main.services.OrderService;
import rs.edu.raf.si.bank2.main.services.UserCommunicationService;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
@Timed
public class OrderController {
    private final OrderService orderService;
    private final UserCommunicationInterface userCommunicationInterface;

    @Autowired
    public OrderController(OrderService orderService, UserCommunicationService communicationService) {
        this.userCommunicationInterface = communicationService;
        this.orderService = orderService;
    }

    @Timed("controllers.order.getAllOrders")
    @GetMapping
    public ResponseEntity<?> getAllOrders() throws ParseException {
        return ResponseEntity.ok().body(this.orderService.findAll());
    }

    @Timed("controllers.order.getAllOrdersByUserId")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAllOrdersByUserId(@PathVariable Long id) throws ParseException {
        return ResponseEntity.ok().body(this.orderService.findAllByUserId(id));
    }

    @Timed("controllers.order.approveOrder")
    @PatchMapping("approve/{id}")
    public ResponseEntity<?> approveOrder(@PathVariable Long id) {
        Optional<Order> order = this.orderService.findById(id);
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Porudzbina nije pronadjena");
        if (order.get().getStatus() != OrderStatus.WAITING)
            return ResponseEntity.badRequest().body("Porudzbina nije u odgovarajucem stanju.");
        return this.orderService.startOrder(id);
    }

    @Timed("controllers.order.denyOrder")
    @PatchMapping("deny/{id}")
    public ResponseEntity<?> denyOrder(@PathVariable Long id) {
        Optional<Order> order = this.orderService.findById(id);
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Order not found");
        if (order.get().getStatus() != OrderStatus.WAITING)
            return ResponseEntity.badRequest().body("Porudzbina nije u odgovarajucem stanju.");
        return ResponseEntity.ok().body(this.orderService.updateOrderStatus(id, OrderStatus.DENIED));
    }

    @Timed("controllers.order.valueOfOrder")
    @GetMapping("value/{id}")
    public ResponseEntity<?> valueOfOrder(@PathVariable Long id) {
        Optional<Order> order = this.orderService.findById(id);
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Order not found");
        Double returnMsg = order.get().getAmount() * order.get().getPrice();
        System.out.println("PRINTAM RETURN MSG" + order.get().getAmount() + " price "
                + order.get().getPrice() + "PORUKA " + returnMsg);
        return ResponseEntity.ok(new CommunicationDto(200, returnMsg.toString()));
    }

    @Timed("controllers.order.tradeTypeOfOrder")
    @GetMapping("tradeType/{id}")
    public ResponseEntity<?> tradeTypeOfOrder(@PathVariable Long id) {
        Optional<Order> order = this.orderService.findById(id);
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Order not found");
        String returnMsg = order.get().getTradeType().toString();
        return ResponseEntity.ok(new CommunicationDto(200, returnMsg));
    }

    @Timed("controllers.order.typeOfOrder")
    @GetMapping("orderType/{id}")
    public ResponseEntity<?> typeOfOrder(@PathVariable Long id) {
        Optional<Order> order = this.orderService.findById(id);
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Order not found");
        String returnMsg = order.get().getOrderType().toString();
        return ResponseEntity.ok(new CommunicationDto(200, returnMsg));
    }
}
