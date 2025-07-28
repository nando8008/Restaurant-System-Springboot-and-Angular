package com.example.restaurant.Controller;

import com.example.restaurant.DTO.OrderDetailsDto;
import com.example.restaurant.DTO.OrderLocationDto;
import com.example.restaurant.DTO.OrderRequestDto;
import com.example.restaurant.Entity.Order;
import com.example.restaurant.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{sessionId}")
    public ResponseEntity<?> placeOrder(@PathVariable String sessionId) {
        try {
            Order order = orderService.placeOrderFromCart(sessionId);
            return ResponseEntity.ok(Map.of(
                    "orderId", order.getId(),
                    "status", "success",
                    "message", "Order placed successfully"
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/inHouseOrderPlace/{orderById}")
    public ResponseEntity<?> placeOrderFromDto(
            @PathVariable String orderById,
            @RequestBody OrderRequestDto dto
    ) {
        try {
            Order order = orderService.placeOrder(orderById, dto);
            return ResponseEntity.ok(Map.of(
                    "orderId", order.getId(),
                    "status", "success",
                    "message", "Order placed successfully"
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/history/{orderById}")
    public ResponseEntity<List<OrderDetailsDto>> getOrdersByUser(@PathVariable String orderById) {
        List<OrderDetailsDto> orders = orderService.getCustomerOrders(orderById);
        return ResponseEntity.ok(orders);
    }
    @PostMapping("/orderLocation")
    public ResponseEntity<?> saveOrderLocation(@RequestBody OrderLocationDto dto) {
        orderService.saveLocationForOrder(dto);
        return ResponseEntity.ok("Location saved");
    }
}
