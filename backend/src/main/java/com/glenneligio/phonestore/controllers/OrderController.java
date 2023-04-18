package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.OrderDto;
import com.glenneligio.phonestore.dtos.OrderItemDto;
import com.glenneligio.phonestore.dtos.XUserDetails;
import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.service.OrderItemService;
import com.glenneligio.phonestore.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private OrderService orderService;
    private OrderItemService orderItemService;

    @Autowired
    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
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

    // SHOULD NOT BE EXPOSED, UPDATES IN ORDER IS DONE ONLY IN DELETE /orders/{id} or /orders/{orderId}/items ENDPOINTS
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder (@PathVariable Long id, @RequestBody OrderEntity orderEntity) {
        OrderEntity entity = orderService.updateOrder(id, orderEntity);
        return ResponseEntity.ok(OrderDto.convertToDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long id,
                                              Authentication authentication) {
        OrderEntity orderEntity = orderService.getOrderById(id);
        isOrderOwnedByUser(orderEntity, authentication);

        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getItemsInOrder(@PathVariable Long orderId,
                                                              Authentication authentication) {
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        return ResponseEntity.ok(orderEntity.getOrderItems().stream().map(OrderItemDto::convertToDto).toList());
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItemDto> addOrderItem(@PathVariable Long orderId,
                                                     @RequestBody OrderItemDto orderItemDto,
                                                     Authentication authentication) {
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        OrderItemEntity orderItemEntity = OrderItemDto.convertToEntity(orderItemDto);
        orderItemEntity.setOrder(orderEntity);

        OrderItemEntity orderItemCreated = orderItemService.createOrderItem(orderItemEntity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/order-items/{id}")
                .buildAndExpand(orderItemCreated.getId())
                .toUri()).body(OrderItemDto.convertToDto(orderItemCreated));
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Long orderId,
                                                        @PathVariable Long orderItemId,
                                                        @RequestBody OrderItemDto orderItemDto,
                                                        Authentication authentication) {
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        OrderItemEntity orderItemEntity = OrderItemDto.convertToEntity(orderItemDto);
        orderItemEntity.setOrder(orderEntity);

        OrderItemEntity updatedOrderItemEntity = orderItemService.updateOrderItem(orderItemId, orderItemEntity);
        return ResponseEntity.ok(OrderItemDto.convertToDto(updatedOrderItemEntity));
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<Object> deleteOrderItem(@PathVariable Long orderId,
                                                  @PathVariable Long orderItemId,
                                                  Authentication authentication) {
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }

    private void isOrderOwnedByUser(OrderEntity orderEntity, Authentication authentication) {
        XUserDetails userDetails = (XUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        if(!orderEntity.getUser().getUsername().equals(username)) throw new ApiException("You can only update order item of your own", HttpStatus.FORBIDDEN);
    }
}
