package com.glenneligio.phonestore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandService brandService;


    private BrandEntity brand1;
    private List<BrandEntity> brandEntityList;

    @BeforeEach
    void setupEach() {
        PhoneEntity phoneEntity1 = PhoneEntity.builder()
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
        brand1 =  BrandEntity.builder().id(0L).name("BrandName1").phoneList(List.of(phoneEntity1)).build();
        BrandEntity brand2 =  BrandEntity.builder().id(1L).name("BrandName2").phoneList(List.of(phoneEntity2)).build();
        brandEntityList = List.of(brand1, brand2);
    }

    @Test
    @DisplayName("Get all brands and return the brands")
    void getAllBrands_returnsBrands() {
        when(brandRepository.findAll()).thenReturn(brandEntityList);

        List<BrandEntity> result = brandService.getAllBrands();

        assertNotNull(result);
        assertNotEquals(0, result.size());
        assertEquals(brandEntityList, result);
    }

    @Test
    @DisplayName("Get brand by id using valid id and return the Brand")
    void getBrandById_usingValidId_returnBrand() {
        Long validId = brand1.getId();
        when(brandRepository.findById(validId)).thenReturn(Optional.of(brand1));

        BrandEntity result = brandService.getBrandById(validId);

        assertNotNull(result);
        assertEquals(brand1, result);
    }

    @Test
    @DisplayName("Get brand by name using invalid name throws ApiException")
    void getBrandByName_usingInvalidName_throwsApiException() {
        String invalidName = "Invalid name";
        when(brandRepository.findByName(invalidName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> brandService.getBrandByName(invalidName));
    }

    @Test
    @DisplayName("Get brand by name using valid name returns Brand")
    void getBrandByName_usingValidName_returnsBrand() {
        String validName = brand1.getName();
        when(brandRepository.findByName(validName)).thenReturn(Optional.of(brand1));

        BrandEntity result = brandService.getBrandByName(validName);

        assertNotNull(result);
        assertEquals(brand1, result);
    }

    @Test
    @DisplayName("Create brand using unused brand name returns newly created Brand")
    void createBrand_usingUnusedBrandName_returnsNewBrand() {
        String unusedName = brand1.getName();
        when(brandRepository.findByName(unusedName)).thenReturn(Optional.empty());
        when(brandRepository.save(brand1)).thenReturn(brand1);

        BrandEntity result = brandService.createBrand(brand1);

        assertNotNull(result);
        assertEquals(brand1, result);
    }

    @Test
    @DisplayName("Create brand using used name throws ApiException")
    void createBrand_usingUsedName_throwsApiException() {
        String usedName = brand1.getName();
        when(brandRepository.findByName(usedName)).thenReturn(Optional.of(brand1));

        assertThrows(ApiException.class, () -> brandService.createBrand(brand1));
    }

    @Test
    @DisplayName("Update brand using invalid name throws ApiException")
    void updateBrandById_usingInvalidName_throwsApiException() {
        Long validId = brand1.getId();
        String invalidName = brand1.getName();
        when(brandRepository.findByName(invalidName)).thenReturn(Optional.of(brand1));
        when(brandRepository.findById(validId)).thenReturn(Optional.of(brand1));

        assertThrows(ApiException.class, () -> brandService.updateBrandById(validId, brand1));
    }

    @Test
    @DisplayName("Update brand using invalid id throws ApiException")
    void updateBrandById_usingInvalidId_throwsApiException() {
        Long invalidId = brand1.getId();
        String validName = "Available name";
        when(brandRepository.findById(invalidId)).thenReturn(Optional.empty());
        when(brandRepository.findByName(validName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> brandService.updateBrandById(invalidId, brand1));
    }

    @Test
    @DisplayName("Update brand using valid id and name returns updated Brand")
    void updateBrandById_usingValidIdAndName_returnsUpdatedBrand() {
        BrandEntity updatedBrand = BrandEntity.builder()
                .name(brand1.getName())
                .phoneList(brand1.getPhoneList())
                .id(brand1.getId())
                .build();
        Long validId = brand1.getId();
        String validName = "Available name";

        when(brandRepository.findById(validId)).thenReturn(Optional.of(brand1));
        when(brandRepository.findByName(validName)).thenReturn(Optional.empty());
        when(brandRepository.save(updatedBrand)).thenReturn(updatedBrand);
        doNothing().when(modelMapper).map(updatedBrand, brand1);

        BrandEntity result = brandService.updateBrandById(validId, updatedBrand);

        assertNotNull(result);
        assertEquals(updatedBrand, result);
    }

    @Test
    @DisplayName("Delete brand using invalid id throws ApiException")
    void deleteBrandById_usingInvalidId_throwsApiException() {
        Long invalidId = brand1.getId();

        when(brandRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> brandService.deleteBrandById(invalidId));
    }

    @Test
    @DisplayName("Delete brand using valid id throws ApiException")
    void deleteBrandById_usingValidId_doesNotThrowException() {
        Long validId = brand1.getId();
        when(brandRepository.findById(validId)).thenReturn(Optional.of(brand1));

        assertDoesNotThrow(() -> brandService.deleteBrandById(validId));
    }

    @Test
    @DisplayName("Get all phones using valid brand name returns a list of phones")
    void getBrandPhones_usingValidName_returnsPhoneList() {
        String validName = brand1.getName();
        when(brandRepository.findByName(validName)).thenReturn(Optional.of(brand1));

        List<PhoneEntity> result = brandService.getBrandPhones(validName);

        assertNotNull(result);
        assertNotEquals(0, result.size());
        assertEquals(brand1.getPhoneList(), result);
    }

    @Test
    @DisplayName("Get all phones using invalid brand name throws ApiException")
    void getBrandPhones_usingInvalidName_throwsApiException() {
        String invalidName = "Invalid brand name";
        when(brandRepository.findByName(invalidName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> brandService.getBrandPhones(invalidName));
    }
}
