package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.OrderItemDto;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-items")
public class OrderItemController {

    private OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems().stream().map(OrderItemDto::convertToDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItem(@PathVariable Long id) {
        OrderItemEntity entity = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(OrderItemDto.convertToDto(entity));
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody OrderItemEntity orderItemEntity) {
        OrderItemEntity orderItemCreated = orderItemService.createOrderItem(orderItemEntity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderItemCreated.getId())
                .toUri()).body(OrderItemDto.convertToDto(orderItemCreated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemEntity orderItemEntity) {
        OrderItemEntity entity = orderItemService.updateOrderItem(id, orderItemEntity);
        return ResponseEntity.ok(OrderItemDto.convertToDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok().build();
    }
}
