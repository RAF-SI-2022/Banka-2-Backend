package rs.edu.raf.si.bank2.securities.controllers;

import java.text.ParseException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.securities.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.securities.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.securities.services.OrderService;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() throws ParseException {
        return ResponseEntity.ok().body(this.orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllOrdersByUserId(@PathVariable Long id) throws ParseException {
        return ResponseEntity.ok().body(this.orderService.findAllByUserId(id));
    }

    @PatchMapping("approve/{id}")
    public ResponseEntity<?> approveOrder(@PathVariable Long id) {
        Optional<Order> order = this.orderService.findById(id);
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Order not found");
        if (order.get().getStatus() == OrderStatus.DENIED)
            return ResponseEntity.badRequest().body("Order is denied ");
        return this.orderService.startOrder(id);
    }

    @PatchMapping("deny/{id}")
    public ResponseEntity<?> denyOrder(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.orderService.updateOrderStatus(id, OrderStatus.DENIED));
    }
}
