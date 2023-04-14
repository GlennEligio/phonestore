package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.model.Phone;
import com.glenneligio.phonestore.service.PhoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phones")
public class PhoneController {

    private final PhoneService service;

    public PhoneController(PhoneService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Phone>> getAllPhones(@RequestParam(name = "brand", required = false) String brandName) {
        if(brandName != null && !brandName.isBlank()) {
            return ResponseEntity.ok(service.getPhoneByBrandName(brandName));
        }
        return ResponseEntity.ok(service.getAllPhones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Phone> getPhoneById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getPhoneById(id));
    }

    @PostMapping
    public ResponseEntity<Phone> createPhone(@RequestBody Phone phone) {
        Phone phoneCreated = service.createPhone(phone);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(phoneCreated.getId())
                .toUri()).body(phoneCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Phone> updatePhone(@RequestBody Phone phone,
                                             @PathVariable("id") Long id) {
        return ResponseEntity.ok(service.updatePhone(id, phone));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhone(@PathVariable("id") Long id) {
        service.deletePhone(id);
        return ResponseEntity.ok().build();
    }

}
