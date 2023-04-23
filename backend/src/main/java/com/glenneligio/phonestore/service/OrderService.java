package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.dtos.OrderDto;
import com.glenneligio.phonestore.dtos.OrderItemDto;
import com.glenneligio.phonestore.dtos.PhoneDto;
import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.OrderStatus;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderService {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";

    private OrderRepository orderRepository;
    private PhoneService phoneService;
    private UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, PhoneService phoneService, UserService userService) {
        this.orderRepository = orderRepository;
        this.phoneService = phoneService;
        this.userService = userService;
    }

    public List<OrderEntity> getAllOrders() {
        final String METHOD_NAME = "getAllOrders";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<OrderEntity> orderEntityList = orderRepository.findAll();
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderEntityList;
    }

    public OrderEntity getOrderById(Long id) {
        final String METHOD_NAME = "getOrderById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order with id " + id + " was not found", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderEntity;
    }

    @Transactional
    public OrderEntity createOrder(OrderEntity orderEntity) {
        final String METHOD_NAME = "createOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        // Check if the user attached exist
        UserEntity userEntity = userService.getUserByUsername(orderEntity.getUser().getUsername());
        orderEntity.setUser(userEntity);

        // check each there is enough stock for each Phone in OrderItem
        List<OrderItemEntity> orderItemEntityList = orderEntity.getOrderItems().stream().map(orderItemEntity -> {
            PhoneEntity phone = phoneService.getPhoneById(orderItemEntity.getPhone().getId());
            if(orderItemEntity.getQuantity() > phone.getQuantity()) throw new ApiException("Not enough quantity for the phone with id " + phone.getId(), HttpStatus.BAD_GATEWAY);
            // reduce the quantity of the phone
            phone.setQuantity(phone.getQuantity() - orderItemEntity.getQuantity());
            PhoneEntity updatedPhone = phoneService.updatePhone(phone.getId(), phone);
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setPhone(updatedPhone);
            return orderItemEntity;
        }).toList();

        orderEntity.setOrderItems(orderItemEntityList);
        orderEntity.setStatus(OrderStatus.PENDING);
        OrderEntity orderSaved = orderRepository.save(orderEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderSaved;
    }

    // NOTE: Update in order is done using order items
    @Transactional
    public OrderEntity updateOrder(Long id, OrderEntity entity) {
        final String METHOD_NAME = "updateOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order with id " + id + " was not found", HttpStatus.NOT_FOUND));
        order.setOrderItems(entity.getOrderItems());
        order.setUser(entity.getUser());
        order.setStatus(entity.getStatus());
        OrderEntity orderEntityUpdated = orderRepository.save(order);
        log.info(EXITING_METHOD, orderEntityUpdated);
        return orderEntityUpdated;
    }

    public void deleteOrder(Long id) {
        final String METHOD_NAME = "deleteOrder";
        log.info(ENTERING_METHOD, METHOD_NAME);
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order with id " + id + " was not found", HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
        log.info(EXITING_METHOD, METHOD_NAME);
    }

    public List<OrderEntity> getOrderByUserUsername(String username) {
        final String METHOD_NAME = "getOrderByUserUsrename";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<OrderEntity> orderEntityList = orderRepository.findByUserUsername(username);
        log.info(EXITING_METHOD, METHOD_NAME);
        return orderEntityList;
    }
}
