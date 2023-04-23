package com.glenneligio.phonestore.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.glenneligio.phonestore.entity.*;
import com.glenneligio.phonestore.enums.OrderStatus;
import com.glenneligio.phonestore.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

@DataJpaTest
public class OrderRepositoryTest {

    private OrderRepository orderRepository;
    private TestEntityManager testEntityManager;

    private OrderEntity orderEntity;
    private UserEntity userEntity;

    @Autowired
    public OrderRepositoryTest(OrderRepository orderRepository, TestEntityManager testEntityManager) {
        this.orderRepository = orderRepository;
        this.testEntityManager = testEntityManager;
    }

    @BeforeEach
    void setupEach() {
        // Create Brand for Phone
        BrandEntity brand1 = BrandEntity.builder().name("Brand Name 1").build();
        BrandEntity brand2 = BrandEntity.builder().name("Brand Name 2").build();
        // Create Phones for OrderItem
        PhoneEntity phoneEntity1 = PhoneEntity.builder()
                .brand(brand1)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(100L)
                .specification("Specification 1")
                .build();
        PhoneEntity phoneEntity2 = PhoneEntity.builder()
                .brand(brand2)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(100L)
                .specification("Specification 1")
                .build();
        // Create OrderItem for Order
        OrderItemEntity orderItem1 = OrderItemEntity.builder()
                .phone(phoneEntity1)
                .quantity(1L)
                .build();
        OrderItemEntity orderItem2 = OrderItemEntity.builder()
                .phone(phoneEntity2)
                .quantity(1L)
                .build();
        // Create User for Order
        userEntity = UserEntity.builder()
                .username("Username1")
                .fullName("Fullname1")
                .isActive(true)
                .password("Password1")
                .userType(UserType.CUSTOMER)
                .email("Email1@gmail.com")
                .build();
        UserEntity user2 = UserEntity.builder()
                .username("Username2")
                .fullName("Fullname2")
                .isActive(true)
                .userType(UserType.CUSTOMER)
                .password("Password2")
                .email("Email2@gmail.com")
                .build();
        // Create Order to persist
        OrderEntity order1 = OrderEntity.builder()
                .status(OrderStatus.PENDING)
                .user(userEntity)
                .orderItems(List.of(orderItem1))
                .build();
        OrderEntity order2 = OrderEntity.builder()
                .status(OrderStatus.PENDING)
                .user(user2)
                .orderItems(List.of(orderItem2))
                .build();
        // Add the Order in the OrderItem to meet constraint validation
        orderItem1.setOrder(order1);
        orderItem2.setOrder(order2);
        // Persist objects
        testEntityManager.persist(brand1);
        testEntityManager.persist(brand2);
        testEntityManager.persist(user2);
        testEntityManager.persist(userEntity);
        testEntityManager.persist(phoneEntity1);
        testEntityManager.persist(phoneEntity2);
        testEntityManager.persist(orderItem1);
        testEntityManager.persist(orderItem2);
        testEntityManager.persist(order1);
        testEntityManager.persist(order2);
    }

    @Test
    @DisplayName("Find orders using valid username and return non-empty Order list")
    void findByUserUsername_usingValidUsername_returnsNonEmptyOrderList() {
        String validUsername = userEntity.getUsername();

        List<OrderEntity> result = orderRepository.findByUserUsername(validUsername);

        assertNotNull(result);
        assertNotEquals(0, result.size());
        assertEquals(validUsername, result.get(0).getUser().getUsername());
    }

    @Test
    @DisplayName("Find orders using invalid username and return empty Order list")
    void findByUserUsername_usingInvalidUsername_returnsEmptyOrderList() {
        String validUsername = "Invalid username";

        List<OrderEntity> result = orderRepository.findByUserUsername(validUsername);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
