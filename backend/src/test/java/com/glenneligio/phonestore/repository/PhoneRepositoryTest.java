package com.glenneligio.phonestore.repository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

@DataJpaTest
@Slf4j
public class PhoneRepositoryTest {

    private TestEntityManager testEntityManager;
    private PhoneRepository phoneRepository;

    private PhoneEntity phoneEntity1;

    @Autowired
    public PhoneRepositoryTest(TestEntityManager testEntityManager, PhoneRepository phoneRepository) {
        this.testEntityManager = testEntityManager;
        this.phoneRepository = phoneRepository;
    }

    @BeforeEach
    void setupEach() {
        BrandEntity brand1 =  BrandEntity.builder().name("BrandName1").build();
        BrandEntity brand2 =  BrandEntity.builder().name("BrandName2").build();
        phoneEntity1 = PhoneEntity.builder()
                .brand(brand1)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(100L)
                .specification("Specification 1")
                .build();
        PhoneEntity phoneEntity2 = PhoneEntity.builder()
                .brand(brand1)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(100L)
                .specification("Specification 1")
                .build();
        testEntityManager.persist(brand1);
        testEntityManager.persist(brand2);
        testEntityManager.persist(phoneEntity1);
        testEntityManager.persist(phoneEntity2);
    }

    @Test
    @DisplayName("Find phones using valid brand name and return non-empty phone list")
    void findByBrandName_usingValidBrandName_returnNonEmptyPhoneList() {
        String validBrandName = phoneEntity1.getBrand().getName();

        List<PhoneEntity> result = phoneRepository.findByBrandName(validBrandName);

        assertNotNull(result);
        assertNotEquals(0, result.size());
        assertEquals(validBrandName, result.get(0).getBrand().getName());
    }

    @Test
    @DisplayName("Find phones using valid brand name and return empty list")
    void findByBrandName_usingInvalidBrandName_returnEmptyPhoneList() {
        String invalidBrandName = "Invalid brand name";

        List<PhoneEntity> result = phoneRepository.findByBrandName(invalidBrandName);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
