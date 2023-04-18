package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private OrderItemRepository orderItemRepository;
    private OrderService orderService;
    private PhoneService phoneService;
    private ModelMapper mapper;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService, PhoneService phoneService, ModelMapper mapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.phoneService = phoneService;
        this.mapper = mapper;
    }

    public List<OrderItemEntity> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public OrderItemEntity getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order item with specified id does not exist", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public OrderItemEntity createOrderItem(OrderItemEntity orderItemEntity) {
        // Check if there is enough stock for the phone
        PhoneEntity phone = phoneService.getPhoneById(orderItemEntity.getPhone().getId());
        if(orderItemEntity.getQuantity() > phone.getQuantity()) throw new ApiException("Phone with id " + phone.getId() + " does not have enough stock", HttpStatus.BAD_REQUEST);
        phone.setQuantity(phone.getQuantity() - orderItemEntity.getQuantity());
        phoneService.updatePhone(phone.getId(), phone);

        OrderEntity orderEntity = orderService.getOrderById(orderItemEntity.getOrder().getId());
        orderItemEntity.setOrder(orderEntity);
        return orderItemRepository.save(orderItemEntity);
    }

    @Transactional
    public OrderItemEntity updateOrderItem(Long id, OrderItemEntity orderItemEntity) {
        OrderItemEntity orderItemEntity1 = orderItemRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order item with specified id does not exist", HttpStatus.NOT_FOUND));
        PhoneEntity newPhone = phoneService.getPhoneById(orderItemEntity.getPhone().getId());
        PhoneEntity oldPhone = phoneService.getPhoneById(orderItemEntity1.getPhone().getId());

        // If the Phone is still the same
        if(orderItemEntity.getPhone().getId().equals(orderItemEntity1.getPhone().getId())) {
            Long quantityChange = orderItemEntity.getQuantity() - orderItemEntity1.getQuantity();
            if(quantityChange > oldPhone.getQuantity()) throw new ApiException("Phone with id " + oldPhone.getId() + " does not have enough stock", HttpStatus.BAD_REQUEST);
            oldPhone.setQuantity(oldPhone.getQuantity() - quantityChange);
            orderItemEntity.setPhone(oldPhone);
        } else {
            // If Phone is not the same anymore, add the quantity back to old phone
            Long quantityReverted = orderItemEntity1.getQuantity();
            oldPhone.setQuantity(oldPhone.getQuantity() + quantityReverted);

            // Check if there is enough stock
            if(orderItemEntity.getQuantity() > newPhone.getQuantity()) throw new ApiException("Phone with id " + newPhone.getId() + " does not have enough stock", HttpStatus.BAD_REQUEST);
            newPhone.setQuantity(newPhone.getQuantity() - orderItemEntity.getQuantity());
            orderItemEntity.setPhone(newPhone);
        }

        mapper.map(orderItemEntity, orderItemEntity1);
        OrderEntity orderEntity = orderService.getOrderById(orderItemEntity.getOrder().getId());
        orderItemEntity.setOrder(orderEntity);
        return orderItemRepository.save(orderItemEntity);
    }

    public void deleteOrderItem(Long id) {
        OrderItemEntity orderItemEntity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order item with specified id does not exist", HttpStatus.NOT_FOUND));
        orderItemRepository.delete(orderItemEntity);
    }

}
