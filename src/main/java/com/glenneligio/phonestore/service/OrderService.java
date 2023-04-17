package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.OrderStatus;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private PhoneService phoneService;
    private UserService userService;
    private ModelMapper mapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, PhoneService phoneService, UserService userService, ModelMapper mapper) {
        this.orderRepository = orderRepository;
        this.phoneService = phoneService;
        this.userService = userService;
        this.mapper = mapper;
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order with id " + id + " was not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public OrderEntity createOrder(OrderEntity orderEntity) {
        // Check if the user attached exist
        UserEntity userEntity = userService.getUserByUsername(orderEntity.getUser().getUsername());
        orderEntity.setUser(userEntity);

        // check each there is enough stock for each Phone in OrderItem
        orderEntity.getOrderItems().forEach(orderItemEntity -> {
            PhoneEntity phone = phoneService.getPhoneById(orderItemEntity.getPhone().getId());
            if(orderItemEntity.getQuantity() > phone.getQuantity()) throw new ApiException("Not enough quantity for the phone with id " + phone.getId(), HttpStatus.BAD_GATEWAY);
            // reduce the quantity of the phone
            phone.setQuantity(phone.getQuantity() - orderItemEntity.getQuantity());
            PhoneEntity updatedPhone = phoneService.updatePhone(phone.getId(), phone);
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setPhone(updatedPhone);
        });
        orderEntity.setStatus(OrderStatus.PENDING);
        return orderRepository.save(orderEntity);
    }

    @Transactional
    public OrderEntity updateOrder(Long id, OrderEntity entity) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order with id " + id + " was not found", HttpStatus.NOT_FOUND));
        mapper.map(entity, order);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order with id " + id + " was not found", HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
    }

    public List<OrderEntity> getOrderByUserUsername(String username) {
        return orderRepository.findByUserUsername(username);
    }
}
