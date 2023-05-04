package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import com.raf.si.Banka2Backend.models.mariadb.orders.OrderStatus;
import com.raf.si.Banka2Backend.services.OrderService;
import java.text.ParseException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        if (!order.isPresent()) return ResponseEntity.badRequest().body("Porudzbina nije pronadjena");
        if (order.get().getStatus() == OrderStatus.DENIED)
            return ResponseEntity.badRequest().body("Porudzbina je odbijena.");
        return this.orderService.startOrder(id);
    }

    @PatchMapping("deny/{id}")
    public ResponseEntity<?> denyOrder(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.orderService.updateOrderStatus(id, OrderStatus.DENIED));
    }
}
