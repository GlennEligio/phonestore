package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.PhoneDto;
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
    public ResponseEntity<List<PhoneDto>> getAllPhones(@RequestParam(name = "brand", required = false) String brandName) {
        List<PhoneDto> phoneDtos = null;
        if(brandName != null && !brandName.isBlank()) {
            phoneDtos = service.getPhoneByBrandName(brandName).stream().map(PhoneDto::convertToDto).toList();
            return ResponseEntity.ok(phoneDtos);
        }
        phoneDtos = service.getAllPhones().stream().map(PhoneDto::convertToDto).toList();
        return ResponseEntity.ok(phoneDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhoneDto> getPhoneById(@PathVariable("id") Long id) {
        PhoneEntity entity = service.getPhoneById(id);
        return ResponseEntity.ok(PhoneDto.convertToDto(entity));
    }

    @PostMapping
    public ResponseEntity<PhoneDto> createPhone(@RequestBody PhoneEntity phoneEntity) {
        PhoneEntity phoneEntityCreated = service.createPhone(phoneEntity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(phoneEntityCreated.getId())
                .toUri()).body(PhoneDto.convertToDto(phoneEntityCreated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneDto> updatePhone(@RequestBody PhoneEntity phoneEntity,
                                                   @PathVariable("id") Long id) {
        PhoneEntity entity = service.updatePhone(id, phoneEntity);
        return ResponseEntity.ok(PhoneDto.convertToDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhone(@PathVariable("id") Long id) {
        service.deletePhone(id);
        return ResponseEntity.ok().build();
    }

}
