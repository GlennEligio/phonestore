package com.glenneligio.phonestore.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.glenneligio.phonestore.entity.BrandEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Slf4j
public class BrandRepositoryTest {

    private BrandRepository brandRepository;
    private TestEntityManager testEntityManager;

    private BrandEntity brand1;

    @Autowired
    public BrandRepositoryTest(BrandRepository brandRepository, TestEntityManager testEntityManager) {
        this.brandRepository = brandRepository;
        this.testEntityManager = testEntityManager;
    }

    @BeforeEach
    public void setupEach() {
        brand1 =  BrandEntity.builder().name("BrandName1").build();
        BrandEntity brand2 =  BrandEntity.builder().name("BrandName2").build();
        testEntityManager.persist(brand1);
        testEntityManager.persist(brand2);
    }

    @Test
    @DisplayName("Find brand using valid brand name and return the brand")
    void findByName_usingValidBrandName_returnsCorrectBrand() {
        String validBrandName = brand1.getName();

        Optional<BrandEntity> brandEntityOptional = brandRepository.findByName(validBrandName);

        assertTrue(brandEntityOptional.isPresent());
        assertEquals(validBrandName, brandEntityOptional.get().getName());
    }

    @Test
    @DisplayName("Find brand using invalid brand name and return an empty Optional")
    void findByName_usingInvalidName_returnsEmptyOptional() {
        String invalidBrandName = "Invalid brandName";

        Optional<BrandEntity> brandEntityOptional = brandRepository.findByName(invalidBrandName);

        assertTrue(brandEntityOptional.isEmpty());
    }
}
