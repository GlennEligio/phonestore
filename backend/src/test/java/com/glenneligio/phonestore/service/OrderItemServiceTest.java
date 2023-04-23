package com.glenneligio.phonestore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.glenneligio.phonestore.dtos.UpdateOrderDto;
import com.glenneligio.phonestore.entity.*;
import com.glenneligio.phonestore.enums.OrderStatus;
import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.OrderItemRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private PhoneService phoneService;

    @InjectMocks
    private OrderItemService orderItemService;
    @InjectMocks
    private ModelMapper mapper;

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
                .orderItems(new ArrayList<>(Collections.singletonList(oi1)))
                .build();
        o2 = OrderEntity.builder()
                .status(OrderStatus.PENDING)
                .user(u2)
                .orderItems(new ArrayList<>(Collections.singletonList(oi1)))
                .build();
        oi1.setOrder(o1);
        oi2.setOrder(o2);
    }

    @Test
    @DisplayName("Get all order items returns order items")
    void getAllOrderItems_returnsOrderItems() {
        var orderItems = List.of(oi1, oi2);
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        var result = orderItemService.getAllOrderItems();

        assertNotNull(result);
        assertEquals(orderItems, result);
    }

    @Test
    @DisplayName("Get order item by id using valid id returns Order item")
    void getOrderItemById_usingValidId_returnsOrderItem() {
        var validId = oi1.getId();
        when(orderItemRepository.findById(validId)).thenReturn(Optional.of(oi1));

        var result = orderItemService.getOrderItemById(validId);

        assertNotNull(result);
        assertEquals(validId, result.getId());
    }

    @Test
    @DisplayName("Get order item by id using invalid id throws ApiException")
    void getOrderItemById_usingInvalidId_throwsApiException() {
        var invalidId = 69420L;
        when(orderItemRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> orderItemService.getOrderItemById(invalidId));
    }

    @Test
    @DisplayName("Create order item with quantity more than phone stock throws ApiException")
    void createOrderItem_usingOrderItemWithQuantityMoreThanStock_throwsApiException() {
        var oiQuantity = 5L;
        var phoneStock = 4L;
        oi1.setQuantity(oiQuantity);
        oi1.getPhone().setQuantity(phoneStock);

        when(phoneService.getPhoneById(oi1.getPhone().getId())).thenReturn(oi1.getPhone());

        assertThrows(ApiException.class, () -> orderItemService.createOrderItem(oi1));
    }

    @Test
    @DisplayName("Create order item with quantity less than phone stock returns Order Item")
    void createOrderItem_usingOrderItemWithQuantityLessThanStock_returnsOrderItem() {
        var initialPhoneStock = oi1.getPhone().getQuantity();
        var updatedOI = OrderItemEntity.builder()
                .id(oi1.getId())
                .phone(oi1.getPhone())
                .quantity(oi1.getQuantity())
                .order(oi1.getOrder())
                .build();
        updatedOI.getPhone().setQuantity(updatedOI.getPhone().getQuantity() - updatedOI.getQuantity());

        when(phoneService.getPhoneById(oi1.getPhone().getId())).thenReturn(oi1.getPhone());
        when(orderService.getOrderById(oi1.getOrder().getId())).thenReturn(oi1.getOrder());
        when(orderItemRepository.save(updatedOI)).thenReturn(updatedOI);

        var result = orderItemService.createOrderItem(oi1);

        assertNotNull(result);
        assertTrue(initialPhoneStock > result.getPhone().getQuantity());
        assertEquals(updatedOI, result);
    }

    @Test
    @DisplayName("Update order item using invalid order item id throws ApiException")
    void updateOrderItem_usingInvalidOrderItem_throwsApiException() {
        var invalidOrderItemId = 69420L;
        when(orderItemRepository.findById(invalidOrderItemId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> orderItemService.deleteOrderItem(invalidOrderItemId));
    }

    @Test
    @DisplayName("Update order item with same phone but quantity requested is more than phone stock throws ApiException")
    void updateOrderItem_usingSamePhoneButIncorrectQuantity_throwsApiException() {
        var validOrderItemId = oi1.getId();
        var newQuantity = 9999999L;
        var updatedOI = OrderItemEntity.builder()
                .phone(oi1.getPhone())
                .quantity(newQuantity)
                .order(oi1.getOrder())
                .build();
        when(orderItemRepository.findById(validOrderItemId)).thenReturn(Optional.of(oi1));
        when(phoneService.getPhoneById(oi1.getPhone().getId())).thenReturn(oi1.getPhone());

        assertThrows(ApiException.class, () -> orderItemService.updateOrderItem(validOrderItemId, updatedOI));
    }

    @Test
    @DisplayName("Update order item with same phone but quantity requested is less than phone stock returns OrderItem")
    void updateOrderItem_usingSamePhoneButCorrectQuantity_throwsApiException() {
        var validOrderItemId = oi1.getId();
        var initialPhoneStock = oi1.getPhone().getQuantity();
        var addedQuantity = 2L;
        var updatedOI = OrderItemEntity.builder()
                .id(oi1.getId())
                .phone(oi1.getPhone())
                .quantity(oi1.getQuantity() + addedQuantity)
                .order(oi1.getOrder())
                .build();
        updatedOI.getPhone().setQuantity(updatedOI.getPhone().getQuantity() - addedQuantity);
        when(orderItemRepository.findById(validOrderItemId)).thenReturn(Optional.of(oi1));
        when(phoneService.getPhoneById(oi1.getPhone().getId())).thenReturn(oi1.getPhone());
        when(orderService.getOrderById(oi1.getOrder().getId())).thenReturn(oi1.getOrder());
        when(orderItemRepository.save(updatedOI)).thenReturn(updatedOI);

        var result = orderItemService.updateOrderItem(validOrderItemId, updatedOI);

        assertNotNull(result);
        assertTrue(initialPhoneStock > result.getPhone().getQuantity());
        assertEquals(updatedOI, result);
    }

    @Test
    @DisplayName("Update order item with new phone but quantity request is more than phone stock throws ApiException")
    void updateOrderItem_usingDifferentPhoneButQuantityRequestMoreThanPhoneStock_throwsApiException() {
        var validOrderItemId = oi1.getId();
        var addedQuantity = 99999L; // VERY HIGH QUANTITY
        var updatedOI = OrderItemEntity.builder()
                .phone(p2)
                .quantity(oi1.getQuantity() + addedQuantity)
                .order(oi1.getOrder())
                .build();
        updatedOI.getPhone().setQuantity(updatedOI.getPhone().getQuantity() - addedQuantity);
        p1.setQuantity(p1.getQuantity() + oi1.getQuantity()); // return the quantity from old item
        when(orderItemRepository.findById(validOrderItemId)).thenReturn(Optional.of(oi1));
        when(phoneService.getPhoneById(p1.getId())).thenReturn(p1);
        when(phoneService.getPhoneById(p2.getId())).thenReturn(p2);

        assertThrows(ApiException.class, () -> orderItemService.updateOrderItem(validOrderItemId, updatedOI));
    }

    @Test
    @DisplayName("Update order item with new phone but quantity request is more than phone stock return updated Order Item")
    void updateOrderItem_usingDifferentPhoneButQuantityRequestLessThanPhoneStock_throwsApiException() {
        var validOrderItemId = oi1.getId();
        var initialOldPhoneStock = p1.getQuantity();
        var initialNewPhoneStock = p2.getQuantity();
        var addedQuantity = 2L;
        var updatedOI = OrderItemEntity.builder()
                .id(oi1.getId())
                .phone(p2)
                .quantity(oi1.getQuantity() + addedQuantity)
                .order(oi1.getOrder())
                .build();
        updatedOI.getPhone().setQuantity(updatedOI.getPhone().getQuantity() - addedQuantity);
        p1.setQuantity(p1.getQuantity() + oi1.getQuantity()); // return the quantity from old item
        when(orderItemRepository.findById(validOrderItemId)).thenReturn(Optional.of(oi1));
        when(phoneService.getPhoneById(p1.getId())).thenReturn(p1);
        when(phoneService.getPhoneById(p2.getId())).thenReturn(p2);
        when(orderService.getOrderById(o1.getId())).thenReturn(o1);
        when(orderItemRepository.save(updatedOI)).thenReturn(updatedOI);

        var result = orderItemService.updateOrderItem(validOrderItemId, updatedOI);

        assertNotNull(result);
        assertTrue(initialNewPhoneStock > result.getPhone().getQuantity());
        assertTrue(initialOldPhoneStock < p1.getQuantity());
        assertEquals(updatedOI, result);
    }

    @Test
    @DisplayName("Delete order item using invalid id throws ApiException")
    void deleteOrderItem_usingInvalidId_throwsApiException() {
        var invalidId = 69240L;
        when(orderItemRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> orderItemService.deleteOrderItem(invalidId));
    }

    @Test
    @DisplayName("Delete order item using valid id does not throw ApiException")
    void deleteOrderItem_usingValidId_throwsApiException() {
        var validId = oi1.getId();
        when(orderItemRepository.findById(validId)).thenReturn(Optional.of(oi1));
        when(orderService.getOrderById(oi1.getOrder().getId())).thenReturn(o1);

        assertDoesNotThrow(() -> orderItemService.deleteOrderItem(validId));
    }
}
