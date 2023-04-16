package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.OrderDto;
import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders().stream().map(OrderDto::convertToDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderEntity orderEntity = orderService.getOrderById(id);
        return ResponseEntity.ok(OrderDto.convertToDto(orderEntity));
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderEntity order) {
        OrderEntity orderCreated = orderService.createOrder(order);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderCreated.getId())
                .toUri()).body(OrderDto.convertToDto(orderCreated));
    }

    // WILL NOT BE EXPOSED, UPDATES IN ORDER IS DONE ONLY ORDER ITEM API ENDPOINTS
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder (@PathVariable Long id, @RequestBody OrderEntity orderEntity) {
        OrderEntity entity = orderService.updateOrder(id, orderEntity);
        return ResponseEntity.ok(OrderDto.convertToDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
