package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.dtos.OrderDto;
import com.glenneligio.phonestore.dtos.OrderItemDto;
import com.glenneligio.phonestore.dtos.PhoneDto;
import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderItemService {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";

    private OrderItemRepository orderItemRepository;
    private OrderService orderService;
    private PhoneService phoneService;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService, PhoneService phoneService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.phoneService = phoneService;
    }

    public List<OrderItemEntity> getAllOrderItems() {
        final String METHOD_NAME = "getAllOrderItems";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<OrderItemEntity> orderItemEntityList = orderItemRepository.findAll();
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderItemEntityList;
    }

    public OrderItemEntity getOrderItemById(Long id) {
        final String METHOD_NAME = "getOrderItemById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderItemEntity orderItemEntity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order item with specified id does not exist", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderItemEntity;
    }

    @Transactional
    public OrderItemEntity createOrderItem(OrderItemEntity orderItemEntity) {
        final String METHOD_NAME = "createOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);

        // Check if there is enough stock for the phone
        PhoneEntity phone = phoneService.getPhoneById(orderItemEntity.getPhone().getId());
        if(orderItemEntity.getQuantity() > phone.getQuantity()) throw new ApiException("Phone with id " + phone.getId() + " does not have enough stock", HttpStatus.BAD_REQUEST);
        phone.setQuantity(phone.getQuantity() - orderItemEntity.getQuantity());
        phoneService.updatePhone(phone.getId(), phone);

        OrderEntity orderEntity = orderService.getOrderById(orderItemEntity.getOrder().getId());
        orderItemEntity.setOrder(orderEntity);
        OrderItemEntity orderItemEntityCreated = orderItemRepository.save(orderItemEntity);

        log.info(EXITING_METHOD, METHOD_NAME);
        return orderItemEntityCreated;
    }

    @Transactional
    public OrderItemEntity updateOrderItem(Long id, OrderItemEntity orderItemEntity) {
        final String METHOD_NAME = "updateOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);

        OrderItemEntity orderItemEntityInDb = orderItemRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order item with specified id does not exist", HttpStatus.NOT_FOUND));
        PhoneEntity newPhone = phoneService.getPhoneById(orderItemEntity.getPhone().getId());
        PhoneEntity oldPhone = phoneService.getPhoneById(orderItemEntityInDb.getPhone().getId());

        // If the Phone is still the same
        if(orderItemEntity.getPhone().getId().equals(orderItemEntityInDb.getPhone().getId())) {
            Long quantityChange = orderItemEntity.getQuantity() - orderItemEntityInDb.getQuantity();

            if(quantityChange > oldPhone.getQuantity()) throw new ApiException("Phone with id " + oldPhone.getId() + " does not have enough stock", HttpStatus.BAD_REQUEST);
            oldPhone.setQuantity(oldPhone.getQuantity() - quantityChange);
            phoneService.updatePhone(oldPhone.getId(), oldPhone);
            orderItemEntity.setPhone(oldPhone);

        } else {
            // If Phone is not the same anymore, add the quantity back to old phone
            Long quantityReverted = orderItemEntityInDb.getQuantity();
            oldPhone.setQuantity(oldPhone.getQuantity() + quantityReverted);

            // Check if there is enough stock
            if(orderItemEntity.getQuantity() > newPhone.getQuantity()) throw new ApiException("Phone with id " + newPhone.getId() + " does not have enough stock", HttpStatus.BAD_REQUEST);
            newPhone.setQuantity(newPhone.getQuantity() - orderItemEntity.getQuantity());

            phoneService.updatePhone(oldPhone.getId(), oldPhone);
            phoneService.updatePhone(newPhone.getId(), newPhone);
            orderItemEntity.setPhone(newPhone);
        }

        // update orderItemEntity1 info
        orderItemEntityInDb.setQuantity(orderItemEntity.getQuantity());
        orderItemEntityInDb.setPhone(orderItemEntity.getPhone());

        // update orderItemEntity1 order
        OrderEntity orderEntity = orderService.getOrderById(orderItemEntity.getOrder().getId());
        orderItemEntityInDb.setOrder(orderEntity);
        OrderItemEntity orderItemEntitySaved = orderItemRepository.save(orderItemEntityInDb);
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderItemEntitySaved;
    }

    @Transactional
    public void deleteOrderItem(Long id) {
        final String METHOD_NAME = "deleteOrderItem";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderItemEntity orderItemEntity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order item with specified id does not exist", HttpStatus.NOT_FOUND));
        OrderEntity orderEntity = orderService.getOrderById(orderItemEntity.getOrder().getId());
        orderEntity.getOrderItems().removeIf(item -> item.getId().equals(id));
        orderItemRepository.delete(orderItemEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
    }

}
