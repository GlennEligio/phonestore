package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.entity.Brand;
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
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Brand> getBrandByName(@PathVariable("name") String brandName) {
        return ResponseEntity.ok(brandService.getBrandByName(brandName));
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
         Brand brand1 = brandService.createBrand(brand);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(brand1.getId())
                .toUri()).body(brand1);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable("id") Long id,
                                             @RequestBody Brand brand) {
        return ResponseEntity.ok(brandService.updateBrandById(id, brand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable("id") Long id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.ok().build();
    }
}
