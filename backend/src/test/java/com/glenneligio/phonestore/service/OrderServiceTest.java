package com.glenneligio.phonestore.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.glenneligio.phonestore.entity.*;
import com.glenneligio.phonestore.enums.OrderStatus;
import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class OrderServiceTest {

    @Mock
    private PhoneService phoneService;
    @Mock
    private UserService userService;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private PhoneEntity p1, p2;
    private BrandEntity b1, b2;
    private OrderItemEntity oi1, oi2;
    private OrderEntity o1, o2;
    private UserEntity u1, u2;

    @BeforeEach
    void setupEach() {
        b1 =  BrandEntity.builder().id(0L).name("BrandName1").build();
        b2 =  BrandEntity.builder().id(1L).name("BrandName2").build();
        p1 = PhoneEntity.builder()
                .id(1L)
                .brand(b1)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(101L)
                .specification("Specification 1")
                .build();
        p2 = PhoneEntity.builder()
                .id(2L)
                .brand(b2)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(102L)
                .specification("Specification 1")
                .build();
        oi1 = OrderItemEntity.builder()
                .id(1L)
                .phone(p1)
                .quantity(1L)
                .build();
        oi2 = OrderItemEntity.builder()
                .id(2L)
                .phone(p2)
                .quantity(2L)
                .build();
        u1 = UserEntity.builder()
                .username("Username1")
                .fullName("Fullname1")
                .isActive(true)
                .password("Password1")
                .userType(UserType.CUSTOMER)
                .email("Email1@gmail.com")
                .build();
        u2 = UserEntity.builder()
                .username("Username2")
                .fullName("Fullname2")
                .isActive(true)
                .userType(UserType.CUSTOMER)
                .password("Password2")
                .email("Email2@gmail.com")
                .build();
        o1 = OrderEntity.builder()
                .status(OrderStatus.PENDING)
                .user(u1)
                .orderItems(new ArrayList<>(List.of(oi1)))
                .build();
        o2 = OrderEntity.builder()
                .status(OrderStatus.PENDING)
                .user(u2)
                .orderItems(new ArrayList<>(List.of(oi2)))
                .build();
        oi1.setOrder(o1);
        oi2.setOrder(o2);
    }

    @Test
    @DisplayName("Get all orders returns all orders")
    void getAllOrders_returnOrders() {
        var orders = List.of(o1, o2);
        when(orderRepository.findAll()).thenReturn(orders);

        var result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(orders, result);
    }

    @Test
    @DisplayName("Get order by id using valid id returns Order")
    void getOrderById_usingValidId_returnsOrder() {
        var validId = o1.getId();
        when(orderRepository.findById(validId)).thenReturn(Optional.of(o1));

        var result = orderService.getOrderById(validId);

        assertNotNull(result);
        assertEquals(o1.getId(), validId);
        assertEquals(o1, result);
    }

    @Test
    @DisplayName("Get order by id using invalid id throws ApiException")
    void getOrderById_usingInvalidId_throwsApiException() {
        var invalidId = 69420L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> orderService.getOrderById(invalidId));
    }

    @Test
    @DisplayName("Create order with quantity request less than phone stock returns new order")
    void createOrder_withQuantityRequestLessThanPhoneStock_returnsNewOrder() {
        var initialPhoneQuantity = p1.getQuantity();
        var quantityRequested = oi1.getQuantity();
        var userOnOrder = UserEntity.builder()
                .username(o1.getUser().getUsername())
                .build();
        var orderToCreate = OrderEntity.builder()
                .orderItems(o1.getOrderItems())
                .user(userOnOrder)
                .build();
        // used for mocking phoneService.updatePhone
        var p1Updated = PhoneEntity.builder()
                .id(p1.getId())
                .brand(p1.getBrand())
                .price(p1.getPrice())
                .description(p1.getDescription())
                .discount(p1.getDiscount())
                .quantity(initialPhoneQuantity - quantityRequested)
                .specification(p1.getSpecification())
                .build();
        var orderCreated = OrderEntity.builder()
                .orderItems(o1.getOrderItems())
                .user(u1)
                .status(OrderStatus.PENDING)
                .build();

        when(userService.getUserByUsername(userOnOrder.getUsername())).thenReturn(u1);
        when(phoneService.getPhoneById(p1.getId())).thenReturn(p1);
        when(phoneService.updatePhone(p1.getId(), p1Updated)).thenReturn(p1Updated);
        when(orderRepository.save(orderCreated)).thenReturn(orderToCreate);

        var result = orderService.createOrder(orderToCreate);

        assertNotNull(result);
        assertEquals(p1Updated, result.getOrderItems().get(0).getPhone());
        assertTrue(initialPhoneQuantity > result.getOrderItems().get(0).getQuantity());
        assertEquals(OrderStatus.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("Create order with quantity requested more than phone stock throws ApiException")
    void createOrder_withQuantityRequestedMoreThanPhoneStock_throwsApiException() {
        var phoneStock = 50L;
        var quantityRequest = 9999L;
        oi1.setQuantity(quantityRequest);
        p1.setQuantity(phoneStock);
        var userOnOrder = UserEntity.builder()
                .username(o1.getUser().getUsername())
                .build();
        var orderToCreate = OrderEntity.builder()
                .orderItems(o1.getOrderItems())
                .user(userOnOrder)
                .build();
        var orderCreated = OrderEntity.builder()
                .orderItems(o1.getOrderItems())
                .user(u1)
                .status(OrderStatus.PENDING)
                .build();
        when(userService.getUserByUsername(userOnOrder.getUsername())).thenReturn(u1);
        when(phoneService.getPhoneById(p1.getId())).thenReturn(p1);

        assertThrows(ApiException.class, () -> orderService.createOrder(o1));
    }

    @Test
    @DisplayName("Update order using invalid id throws ApiException")
    void updateOrder_withInvalidId_throwsApiException() {
        var invalidId = 69420L;
        var o1Updated = OrderEntity.builder()
                .status(OrderStatus.COMPLETED)
                .user(u2)
                .orderItems(o2.getOrderItems())
                .build();
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> orderService.updateOrder(invalidId, o1Updated));
    }

    @Test
    @DisplayName("Update order using valid id returns updated Order")
    void updateOrder_withValidId_returnsUpdatedOrder() {
        var validId = o1.getId();
        var o1Updated = OrderEntity.builder()
                .status(OrderStatus.COMPLETED)
                .user(u2)
                .orderItems(o2.getOrderItems())
                .build();
        when(orderRepository.findById(validId)).thenReturn(Optional.of(o1));
        when(orderRepository.save(o1Updated)).thenReturn(o1Updated);

        var result = orderService.updateOrder(validId, o1Updated);

        assertNotNull(result);
        assertEquals(o1Updated, result);
    }

    @Test
    @DisplayName("Delete order using invalid id throws ApiException")
    void deleteOrder_withInvalidId_throwsApiException() {
        var invalidId = 69420L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> orderService.deleteOrder(invalidId));
    }

    @Test
    @DisplayName("Delete order using valid id does not throw ApiException")
    void deleteOrder_withValidId_doesNotThrowApiException() {
        var validId = o1.getId();
        when(orderRepository.findById(validId)).thenReturn(Optional.of(o1));

        assertDoesNotThrow(() -> orderService.deleteOrder(validId));
    }

    @Test
    @DisplayName("Get order by user's username returns orders with correct user username")
    void getOrderByUserUsername_returnsOrdersWithSameUserUsername() {
        var username = o1.getUser().getUsername();
        var orders = List.of(o1);
        when(orderRepository.findByUserUsername(username)).thenReturn(orders);

        var result = orderService.getOrderByUserUsername(username);

        assertNotNull(result);
        assertTrue(result.stream().allMatch(o -> o.getUser().getUsername().equals(username)));
    }
}
