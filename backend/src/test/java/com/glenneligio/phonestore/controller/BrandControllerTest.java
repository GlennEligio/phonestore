package com.glenneligio.phonestore.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.glenneligio.phonestore.dtos.BrandDto;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BrandControllerTest {

    private static final String BASE_BRAND_URI = "/api/v1/brands";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    private BrandEntity b1, b2;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        b1 =  BrandEntity.builder().id(1L).name("BrandName1").build();
        b2 =  BrandEntity.builder().id(2L).name("BrandName2").build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should create MockMvc instance")
    void mockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("Fetch brand list returns brand list dto")
    void getAllBrands_returnsBrandDtoList() throws Exception {
        List<BrandEntity> brandEntityList = List.of(b1, b2);
        when(brandService.getAllBrands()).thenReturn(brandEntityList);
        List<BrandDto> response = brandEntityList.stream().map(BrandDto::convertToDto).toList();
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Fetch brand by brand name using valid name returns brand dto response")
    void getBrandByName_usingValidName_returnBrandDto() throws Exception {
        var validName = b1.getName();
        when(brandService.getBrandByName(validName)).thenReturn(b1);
        BrandDto response = BrandDto.convertToDto(b1);
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(get(BASE_BRAND_URI + "/" + validName))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Fetch brand by brand name using invalid name returns 404 NOT FOUND")
    void getBrandByName_usingInvalidName_returns404NotFound() throws Exception {
        var invalidName = "Invalid name";
        when(brandService.getBrandByName(invalidName)).thenThrow(new ApiException("No brand found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get(BASE_BRAND_URI + "/" + invalidName))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create brand using valid brand dto returns new Brand Dto")
    void createBrand_usingValidBrandDtoInput_returns200OKWithBrand() throws Exception {
        var requestBody = BrandDto.convertToDto(BrandEntity.builder()
                .name(b1.getName()).build());
        var requestJson = mapper.writeValueAsString(requestBody);
        var responseJson = mapper.writeValueAsString(BrandDto.convertToDto(b1));
        when(brandService.createBrand(BrandDto.convertToEntity(requestBody))).thenReturn(b1);

        mockMvc.perform(post(BASE_BRAND_URI)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create brand using unavailable brand name returns 400 BAD REQUEST")
    void createBrand_usingUnavailableName_returns400BadRequest() throws Exception {
        var unavailableName = "TakenName";
        var requestBody = BrandDto.convertToDto(BrandEntity.builder()
                .name(unavailableName).build());
        var requestJson = mapper.writeValueAsString(requestBody);
        when(brandService.createBrand(BrandDto.convertToEntity(requestBody))).thenThrow(new ApiException("Name already taken", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(BASE_BRAND_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create brand using invalid request body returns 400 BAD REQUEST")
    void createBrand_usingInvalidRequestBody_returns400BadRequest() throws Exception {
        var invalidName = "";
        var requestBody = BrandDto.convertToDto(BrandEntity.builder()
                .name(invalidName).build());
        var requestJson = mapper.writeValueAsString(requestBody);
        mockMvc.perform(post(BASE_BRAND_URI)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update brand using invalid id returns 404 NOT FOUND")
    void updateBrand_usingInvalidId_returns404NotFound() throws Exception {
        var invalidId = 69420L;
        var requestBody = BrandDto.convertToDto(BrandEntity.builder()
                .name("NewBrand").build());
        var requestJson = mapper.writeValueAsString(requestBody);
        when(brandService.updateBrandById(invalidId, BrandDto.convertToEntity(requestBody))).thenThrow(new ApiException("Brand not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(put(BASE_BRAND_URI + "/" + invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update brand using valid id but invalid request body returns 400 BAD REQUEST")
    void updateBrand_usingValidIdButInvalidBody_returns400BadRequest() throws Exception {
        var validId = b1.getId();
        var invalidName = "";
        var requestBody = BrandDto.convertToDto(BrandEntity.builder()
                .name(invalidName).build());
        var requestJson = mapper.writeValueAsString(requestBody);

        mockMvc.perform(put(BASE_BRAND_URI + "/" + validId)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update brand using valid id and request body returns 200 OK with updated Brand")
    void updateBrand_usingValidIdAndRequestBody_returns200OK() throws Exception {
        var validId = b1.getId();
        var newName = "NewBrandName";
        var requestBody = BrandDto.convertToDto(BrandEntity.builder()
                .name(newName).build());
        var requestJson = mapper.writeValueAsString(requestBody);
        var updatedBrandEntity = BrandDto.convertToEntity(requestBody);
        var responseJson = mapper.writeValueAsString(BrandDto.convertToDto(updatedBrandEntity));
        when(brandService.updateBrandById(validId, BrandDto.convertToEntity(requestBody))).thenReturn(updatedBrandEntity);

        mockMvc.perform(put(BASE_BRAND_URI + "/" + validId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Delete brand using invalid id returns 404 NOT FOUND")
    void deleteBrand_usingInvalidId_returns404NotFound() throws Exception {
        var invalidId = 69420L;
        doThrow(new ApiException("Brand not found", HttpStatus.NOT_FOUND)).when(brandService).deleteBrandById(invalidId);

        mockMvc.perform(delete(BASE_BRAND_URI + "/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete brand using valid id returns 200 OK")
    void deleteBrand_usingValidId_returns200OK() throws Exception {
        var validId = b1.getId();
        doNothing().when(brandService).deleteBrandById(validId);

        mockMvc.perform(delete(BASE_BRAND_URI + "/" + validId))
                .andExpect(status().isOk());
    }
}
