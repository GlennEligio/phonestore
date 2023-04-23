package com.glenneligio.phonestore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.PhoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class PhoneServiceTest {

    @Mock
    private PhoneRepository phoneRepository;
    @Mock
    private BrandService brandService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PhoneService phoneService;

    private PhoneEntity phoneEntity1;
    private BrandEntity brandEntity1, brandEntity2;
    private List<PhoneEntity> phoneEntityList;

    @BeforeEach
    void setupEach() {
        brandEntity1 =  BrandEntity.builder().id(0L).name("BrandName1").build();
        brandEntity2 =  BrandEntity.builder().id(1L).name("BrandName2").build();
        phoneEntity1 = PhoneEntity.builder()
                .id(1L)
                .brand(brandEntity1)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(100L)
                .specification("Specification 1")
                .brand(brandEntity1)
                .build();
        PhoneEntity phoneEntity2 = PhoneEntity.builder()
                .id(2L)
                .brand(brandEntity1)
                .price(100.0)
                .description("Description 1")
                .discount(0.1)
                .quantity(100L)
                .specification("Specification 1")
                .brand(brandEntity2)
                .build();
        phoneEntityList = List.of(phoneEntity1, phoneEntity2);
    }


    @Test
    @DisplayName("Get all phones returns phone list")
    void getAllPhones_returnsPhoneList() {
        when(phoneRepository.findAll()).thenReturn(phoneEntityList);

        var result = phoneService.getAllPhones();

        assertNotNull(result);
        assertNotEquals(0, result.size());
        assertEquals(phoneEntityList, result);
    }

    @Test
    @DisplayName("Get phone by id using invalid id throws ApiException")
    void getPhoneById_usingInvalidId_throwsApiException() {
        Long invalidId = 69240L;
        when(phoneRepository.findById(invalidId)).thenThrow(new ApiException("Phone does not exist", HttpStatus.NOT_FOUND));

        assertThrows(ApiException.class, () -> phoneService.getPhoneById(invalidId));
    }

    @Test
    @DisplayName("Get phone by id using valid id returns Phone")
    void getPhoneById_usingValidId_returnsPhone() {
        Long validId = phoneEntity1.getId();
        when(phoneRepository.findById(validId)).thenReturn(Optional.of(phoneEntity1));

        PhoneEntity result = phoneService.getPhoneById(validId);

        assertNotNull(result);
        assertEquals(phoneEntity1, result);
    }

    @Test
    @DisplayName("Get phones by brand name using valid name returns list of Phone with correct brand")
    void getPhonesByBrandName_usingValidName_returnsListOfPhone() {
        String validName = brandEntity1.getName();
        List<PhoneEntity> filteredPhoneList = phoneEntityList.stream().filter(p -> p.getBrand().getName().equals(validName)).toList();
        when(phoneRepository.findByBrandName(validName)).thenReturn(filteredPhoneList);

        List<PhoneEntity> result = phoneService.getPhonesByBrandName(validName);

        assertNotNull(result);
        assertTrue(result.stream().allMatch(p -> p.getBrand().getName().equals(validName)));
    }

    @Test
    @DisplayName("Create phone using phone with valid brand returns new Phone")
    void createPhone_usingPhoneWithValidBrand_returnsNewPhone() {
        String validBrandName = brandEntity1.getName();
        when(brandService.getBrandByName(validBrandName)).thenReturn(brandEntity1);
        when(phoneRepository.save(phoneEntity1)).thenReturn(phoneEntity1);

        PhoneEntity result = phoneService.createPhone(phoneEntity1);

        assertNotNull(result);
        assertEquals(result.getBrand(), brandEntity1);
        assertEquals(phoneEntity1, result);
    }

    @Test
    @DisplayName("Update phone using valid id returns updated Phone")
    void updatePhone_usingValidId_returnsUpdatedPhone() {
        Long validId = phoneEntity1.getId();
        String validBrandName = brandEntity2.getName();
        PhoneEntity updatedPhone = PhoneEntity.builder()
                .id(phoneEntity1.getId())
                .brand(brandEntity2) // only this changed
                .specification(phoneEntity1.getSpecification())
                .quantity(phoneEntity1.getQuantity())
                .discount(phoneEntity1.getDiscount())
                .price(phoneEntity1.getPrice())
                .build();
        when(phoneRepository.findById(validId)).thenReturn(Optional.of(phoneEntity1));
        when(brandService.getBrandByName(validBrandName)).thenReturn(brandEntity2);
        when(phoneRepository.save(updatedPhone)).thenReturn(updatedPhone);

        PhoneEntity result = phoneService.updatePhone(validId, updatedPhone);

        assertNotNull(result);
        assertEquals(updatedPhone, result);
    }

    @Test
    @DisplayName("Update phone using invalid id throws ApiException")
    void updatePhone_usingInvalidId_throwsApiException() {
        Long invalidId = 68L;
        String validBrandName = brandEntity2.getName();
        PhoneEntity updatedPhone = PhoneEntity.builder()
                .id(phoneEntity1.getId())
                .brand(brandEntity2) // only this changed
                .specification(phoneEntity1.getSpecification())
                .quantity(phoneEntity1.getQuantity())
                .discount(phoneEntity1.getDiscount())
                .price(phoneEntity1.getPrice())
                .build();
        when(phoneRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> phoneService.updatePhone(invalidId, updatedPhone));
    }

    @Test
    @DisplayName("Delete phone using valid id does not throw ApiException")
    void deletePhone_usingValidId_doesNotThrowApiException() {
        Long validId = phoneEntity1.getId();
        when(phoneRepository.findById(validId)).thenReturn(Optional.of(phoneEntity1));

        assertDoesNotThrow(() -> phoneService.deletePhone(validId));
    }

    @Test
    @DisplayName("Delete phone using invalid id throws ApiException")
    void deletePhone_usingInvalidId_throwsApiException() {
        Long invalidId = 69L;
        when(phoneRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> phoneService.deletePhone(invalidId));
    }
}
