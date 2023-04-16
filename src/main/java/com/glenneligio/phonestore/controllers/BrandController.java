package com.glenneligio.phonestore.controllers;

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
    public ResponseEntity<List<BrandEntity>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{name}")
    public ResponseEntity<BrandEntity> getBrandByName(@PathVariable("name") String brandName) {
        return ResponseEntity.ok(brandService.getBrandByName(brandName));
    }

    @PostMapping
    public ResponseEntity<BrandEntity> createBrand(@RequestBody BrandEntity brandEntity) {
         BrandEntity brandEntity1 = brandService.createBrand(brandEntity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(brandEntity1.getId())
                .toUri()).body(brandEntity1);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandEntity> updateBrand(@PathVariable("id") Long id,
                                                   @RequestBody BrandEntity brandEntity) {
        return ResponseEntity.ok(brandService.updateBrandById(id, brandEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable("id") Long id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.ok().build();
    }
}
