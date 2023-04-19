package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.*;
import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.service.OrderItemService;
import com.glenneligio.phonestore.service.OrderService;
import jakarta.validation.Valid;
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
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";
    public static final String SERVICE_RESPONSE = "Service response: {}";
    private OrderService orderService;
    private OrderItemService orderItemService;

    @Autowired
    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        final String METHOD_NAME = "getAllOrders";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<OrderEntity> orderEntityList = orderService.getAllOrders();
        List<OrderDto> orderDtoList = orderEntityList.stream().map(OrderDto::convertToDto).toList();
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetch all orders. Count: {}", orderDtoList.size());
        log.debug(SERVICE_RESPONSE, orderDtoList);
        return ResponseEntity.ok(orderDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        final String METHOD_NAME = "getOrderById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderEntity orderEntity = orderService.getOrderById(id);
        OrderDto orderDto = OrderDto.convertToDto(orderEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetch order with id {}", id);
        log.debug(SERVICE_RESPONSE, orderDto);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderDto orderDto,
                                                Authentication authentication) {
        final String METHOD_NAME = "createOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Creating order with information {}", orderDto);

        OrderEntity orderEntityInput = CreateOrderDto.convertToEntity(orderDto);

        String username = ((XUserDetails) authentication.getPrincipal()).getUsername();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        orderEntityInput.setUser(user);

        OrderEntity orderCreated = orderService.createOrder(orderEntityInput);
        OrderDto orderDtoResponse = OrderDto.convertToDto(orderCreated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully created order.");
        log.debug(SERVICE_RESPONSE, orderDtoResponse);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderCreated.getId())
                .toUri()).body(orderDtoResponse);
    }

    // SHOULD NOT BE EXPOSED, UPDATES IN ORDER IS DONE ONLY IN
    // DELETE /orders/{id} and
    // DELETE/UPDATE/POST /orders/{orderId}/items ENDPOINTS
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder (@PathVariable Long id,
                                                 @RequestBody @Valid UpdateOrderDto orderDto,
                                                 Authentication authentication) {
        final String METHOD_NAME = "updateOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Updating order with id {}, and info {}", id, orderDto);

        OrderEntity orderEntityInput = UpdateOrderDto.convertToEntity(orderDto);

        String username = ((XUserDetails) authentication.getPrincipal()).getUsername();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        orderEntityInput.setUser(user);

        OrderEntity orderEntityUpdated = orderService.updateOrder(id, orderEntityInput);
        OrderDto orderDtoResponse = OrderDto.convertToDto(orderEntityUpdated);

        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug(SERVICE_RESPONSE, orderDtoResponse);
        return ResponseEntity.ok(orderDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long id,
                                              Authentication authentication) {
        final String METHOD_NAME = "deleteOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Deleting order with id {}", id);
        OrderEntity orderEntity = orderService.getOrderById(id);
        isOrderOwnedByUser(orderEntity, authentication);

        orderService.deleteOrder(id);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully deleted order with id {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getItemsInOrder(@PathVariable Long orderId,
                                                              Authentication authentication) {
        final String METHOD_NAME = "getItemsInOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Getting order items on order with id {}", orderId);
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        List<OrderItemDto> orderItemDtoList = orderEntity.getOrderItems().stream().map(OrderItemDto::convertToDto).toList();
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetch all order items for order with id {}. Order item: {}", orderEntity, orderItemDtoList);
        return ResponseEntity.ok(orderItemDtoList);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItemDto> addOrderItem(@PathVariable Long orderId,
                                                     @RequestBody @Valid OrderItemDto orderItemDto,
                                                     Authentication authentication) {
        final String METHOD_NAME = "addOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Adding new order item for order with id {}, and order item {}", orderId, orderItemDto);
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        OrderItemEntity orderItemEntity = OrderItemDto.convertToEntity(orderItemDto);
        orderItemEntity.setOrder(orderEntity);

        OrderItemEntity orderItemCreated = orderItemService.createOrderItem(orderItemEntity);
        OrderItemDto orderItemDtoCreated = OrderItemDto.convertToDto(orderItemCreated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully added an order item in order {}", orderItemDtoCreated);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/order-items/{id}")
                .buildAndExpand(orderItemCreated.getId())
                .toUri()).body(orderItemDtoCreated);
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Long orderId,
                                                        @PathVariable Long orderItemId,
                                                        @RequestBody @Valid OrderItemDto orderItemDto,
                                                        Authentication authentication) {
        final String METHOD_NAME = "updateOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Editing order item with id {}, on order with id {}, with info {}", orderItemId, orderId, orderItemDto);
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        OrderItemEntity orderItemEntity = OrderItemDto.convertToEntity(orderItemDto);
        orderItemEntity.setOrder(orderEntity);

        OrderItemEntity updatedOrderItemEntity = orderItemService.updateOrderItem(orderItemId, orderItemEntity);
        OrderItemDto updatedOrderItemDto = OrderItemDto.convertToDto(updatedOrderItemEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully edited the order item with id {}, with info {}", orderItemId, updatedOrderItemDto);
        return ResponseEntity.ok(updatedOrderItemDto);
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<Object> deleteOrderItem(@PathVariable Long orderId,
                                                  @PathVariable Long orderItemId,
                                                  Authentication authentication) {
        final String METHOD_NAME = "deleteOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Deleting order item with id {}, inside order with id {}", orderItemId, orderId);
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        isOrderOwnedByUser(orderEntity, authentication);

        orderItemService.deleteOrderItem(orderItemId);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully deleted order item with id {}, inside order with id {}", orderItemId, orderId);
        return ResponseEntity.ok().build();
    }

    private void isOrderOwnedByUser(OrderEntity orderEntity, Authentication authentication) {
        final String METHOD_NAME = "isOrderOwnedByUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Checking if order with id {} is owned by authentication {}", orderEntity.getId(), authentication);
        XUserDetails userDetails = (XUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        if(!orderEntity.getUser().getUsername().equals(username)) throw new ApiException("You can only update order item of your own", HttpStatus.FORBIDDEN);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.info("Order with id {} is owned by authentication with username {}", orderEntity.getId(), username);
    }
}
