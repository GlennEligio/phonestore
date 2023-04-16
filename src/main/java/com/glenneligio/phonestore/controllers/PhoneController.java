package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phones")
public class PhoneController {

    private final PhoneService service;

    @Autowired
    public PhoneController(PhoneService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PhoneEntity>> getAllPhones(@RequestParam(name = "brand", required = false) String brandName) {
        if(brandName != null && !brandName.isBlank()) {
            return ResponseEntity.ok(service.getPhoneByBrandName(brandName));
        }
        return ResponseEntity.ok(service.getAllPhones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhoneEntity> getPhoneById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getPhoneById(id));
    }

    @PostMapping
    public ResponseEntity<PhoneEntity> createPhone(@RequestBody PhoneEntity phoneEntity) {
        PhoneEntity phoneEntityCreated = service.createPhone(phoneEntity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(phoneEntityCreated.getId())
                .toUri()).body(phoneEntityCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneEntity> updatePhone(@RequestBody PhoneEntity phoneEntity,
                                                   @PathVariable("id") Long id) {
        return ResponseEntity.ok(service.updatePhone(id, phoneEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhone(@PathVariable("id") Long id) {
        service.deletePhone(id);
        return ResponseEntity.ok().build();
    }

}
