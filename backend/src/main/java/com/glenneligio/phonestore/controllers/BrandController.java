package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.BrandDto;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<BrandDto> brandDtos = brandService.getAllBrands().stream().map(BrandDto::convertToDto).toList();
        return ResponseEntity.ok(brandDtos);
    }

    @GetMapping("/{name}")
    public ResponseEntity<BrandDto> getBrandByName(@PathVariable("name") String brandName) {
        BrandEntity entity = brandService.getBrandByName(brandName);
        return ResponseEntity.ok(BrandDto.convertToDto(entity));
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody @Valid BrandDto dto) {
        BrandEntity brandEntityInput = BrandDto.convertToEntity(dto);
        BrandEntity brandEntityCreated = brandService.createBrand(brandEntityInput);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(brandEntityCreated.getId())
                .toUri()).body(BrandDto.convertToDto(brandEntityCreated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable("id") Long id,
                                                @RequestBody @Valid BrandDto brandDto) {
        BrandEntity brandEntityInput = BrandDto.convertToEntity(brandDto);
        BrandEntity brandEntityUpdated = brandService.updateBrandById(id, brandEntityInput);
        return ResponseEntity.ok(BrandDto.convertToDto(brandEntityUpdated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable("id") Long id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.ok().build();
    }
}
