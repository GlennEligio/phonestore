package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.BrandDto;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.service.BrandService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@Slf4j
public class BrandController {

    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";
    public static final String SERVICE_RESPONSE = "Service response: {}";
    private BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        final String METHOD_NAME = "getAllBrands";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<BrandDto> brandDtos = brandService.getAllBrands().stream().map(BrandDto::convertToDto).toList();
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetch all brands. Brand count is {}", brandDtos.size());
        return ResponseEntity.ok(brandDtos);
    }

    @GetMapping("/{name}")
    public ResponseEntity<BrandDto> getBrandByName(@PathVariable("name") String brandName) {
        final String METHOD_NAME = "getBrandByName";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity entity = brandService.getBrandByName(brandName);
        BrandDto brandDto = BrandDto.convertToDto(entity);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug(SERVICE_RESPONSE, brandDto);
        return ResponseEntity.ok(brandDto);
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody @Valid BrandDto brandDto) {
        final String METHOD_NAME = "createBrand";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity brandEntityInput = BrandDto.convertToEntity(brandDto);
        BrandEntity brandEntityCreated = brandService.createBrand(brandEntityInput);
        BrandDto brandDtoResponse = BrandDto.convertToDto(brandEntityCreated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug(SERVICE_RESPONSE, brandDtoResponse);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(brandEntityCreated.getId())
                .toUri()).body(brandDtoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable("id") Long id,
                                                @RequestBody @Valid BrandDto brandDto) {
        final String METHOD_NAME = "updateBrand";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity brandEntityInput = BrandDto.convertToEntity(brandDto);
        BrandEntity brandEntityUpdated = brandService.updateBrandById(id, brandEntityInput);
        BrandDto brandDtoResponse = BrandDto.convertToDto(brandEntityUpdated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Update brand dto info: {}", brandDto);
        log.debug(SERVICE_RESPONSE, brandDtoResponse);
        return ResponseEntity.ok(brandDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable("id") Long id) {
        final String METHOD_NAME = "deleteBrand";
        log.info(ENTERING_METHOD, METHOD_NAME);
        brandService.deleteBrandById(id);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Brand id deleted {}", id);
        return ResponseEntity.ok().build();
    }
}
