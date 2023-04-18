package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.BrandDto;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.service.BrandService;
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
    public ResponseEntity<BrandDto> createBrand(@RequestBody BrandEntity brandEntity) {
         BrandEntity brandEntity1 = brandService.createBrand(brandEntity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(brandEntity1.getId())
                .toUri()).body(BrandDto.convertToDto(brandEntity1));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable("id") Long id,
                                                   @RequestBody BrandEntity brandEntity) {
        BrandEntity entity = brandService.updateBrandById(id, brandEntity);
        return ResponseEntity.ok(BrandDto.convertToDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable("id") Long id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.ok().build();
    }
}
