package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.PhoneDto;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.service.PhoneService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phones")
@Slf4j
public class PhoneController {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";
    public static final String SERVICE_RESPONSE = "Service response: {}";

    private final PhoneService service;

    @Autowired
    public PhoneController(PhoneService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PhoneDto>> getAllPhones(@RequestParam(name = "brand", required = false) String brandName) {
        final String METHOD_NAME = "getAllPhones";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<PhoneDto> phoneDtos = null;
        if(brandName != null && !brandName.isBlank()) {
            log.debug("Fetching all phones with brand name {}", brandName);
            phoneDtos = service.getPhoneByBrandName(brandName).stream().map(PhoneDto::convertToDto).toList();
        } else {
            log.debug("Fetching all phones");
            phoneDtos = service.getAllPhones().stream().map(PhoneDto::convertToDto).toList();

        }

        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetched all phones. Count: {}", phoneDtos.size());
        return ResponseEntity.ok(phoneDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhoneDto> getPhoneById(@PathVariable("id") Long id) {
        final String METHOD_NAME = "getPhoneById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Fetching phone with id {}", id);

        PhoneEntity entity = service.getPhoneById(id);
        PhoneDto phoneDto = PhoneDto.convertToDto(entity);

        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetch phone with id {}", id);
        log.debug(SERVICE_RESPONSE, phoneDto);
        return ResponseEntity.ok(phoneDto);
    }

    @PostMapping
    public ResponseEntity<PhoneDto> createPhone(@RequestBody @Valid PhoneDto phoneDto) {
        final String METHOD_NAME = "createPhone";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Creating new phone with info {}", phoneDto);

        PhoneEntity phoneEntityInput = PhoneDto.convertToEntity(phoneDto);
        PhoneEntity phoneEntityCreated = service.createPhone(phoneEntityInput);

        PhoneDto phoneDtoCreated = PhoneDto.convertToDto(phoneEntityCreated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully create a new phone");
        log.debug(SERVICE_RESPONSE, phoneDtoCreated);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(phoneEntityCreated.getId())
                .toUri()).body(phoneDtoCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneDto> updatePhone(@RequestBody @Valid PhoneDto phoneDto,
                                                @PathVariable("id") Long id) {
        final String METHOD_NAME = "updatePhone";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.info("Updating phone with id {}, and info {}", id, phoneDto);

        PhoneEntity phoneEntityInput = PhoneDto.convertToEntity(phoneDto);
        PhoneEntity phoneEntityUpdated = service.updatePhone(id, phoneEntityInput);
        PhoneDto phoneDtoUpdated = PhoneDto.convertToDto(phoneEntityUpdated);

        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully updated phone with id {}", id);
        log.debug(SERVICE_RESPONSE, phoneDtoUpdated);
        return ResponseEntity.ok(phoneDtoUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhone(@PathVariable("id") Long id) {
        final String METHOD_NAME = "deletePhone";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Deleting phone with id {}", id);
        service.deletePhone(id);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully deleted phone with id {}", id);
        return ResponseEntity.ok().build();
    }

}
