package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.OrderDto;
import com.glenneligio.phonestore.dtos.OrderItemDto;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-items")
@Slf4j
public class OrderItemController {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";
    public static final String SERVICE_RESPONSE = "Service response: {}";

    private OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getAllOrderItems() {
        final String METHOD_NAME = "getAllOrderItems";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<OrderItemDto> orderItemDtoList = orderItemService.getAllOrderItems().stream().map(OrderItemDto::convertToDto).toList();
        log.info(EXITING_METHOD, METHOD_NAME);
        return ResponseEntity.ok(orderItemDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItem(@PathVariable Long id) {
        final String METHOD_NAME = "getOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderItemEntity entity = orderItemService.getOrderItemById(id);
        OrderItemDto orderItemDto = OrderItemDto.convertToDto(entity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return ResponseEntity.ok(orderItemDto);
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody @Valid OrderItemDto orderItemDto) {
        final String METHOD_NAME = "createOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Created an order item with info {}", orderItemDto);
        OrderItemEntity orderItemEntityInput = OrderItemDto.convertToEntity(orderItemDto);
        OrderItemEntity orderItemCreated = orderItemService.createOrderItem(orderItemEntityInput);
        OrderItemDto orderItemDtoCreated = OrderItemDto.convertToDto(orderItemCreated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully created order item");
        log.debug(SERVICE_RESPONSE, orderItemDtoCreated);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderItemCreated.getId())
                .toUri()).body(orderItemDtoCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Long id,
                                                        @RequestBody @Valid OrderItemDto orderItemDto) {
        final String METHOD_NAME = "updateOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Updated order item with id {} and info {}", id, orderItemDto);

        OrderItemEntity orderItemEntityInput = OrderItemDto.convertToEntity(orderItemDto);
        OrderItemEntity orderItemEntityUpdated = orderItemService.updateOrderItem(id, orderItemEntityInput);
        OrderItemDto orderItemDtoCreated = OrderItemDto.convertToDto(orderItemEntityUpdated);

        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully updated order item");
        log.debug(SERVICE_RESPONSE, orderItemDtoCreated);
        return ResponseEntity.ok(orderItemDtoCreated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrderItem(@PathVariable Long id) {
        final String METHOD_NAME = "deleteOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Deleting order item with id {id}");
        orderItemService.deleteOrderItem(id);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully deleted order item with id {}", id);
        return ResponseEntity.ok().build();
    }
}
