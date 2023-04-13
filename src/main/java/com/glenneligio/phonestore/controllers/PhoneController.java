package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.model.Phone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
public class PhoneController {

    @GetMapping
    public ResponseEntity<List<Phone>> getAllPhones() {
        Phone p1 = new Phone(1L, 100.0, 10L, "Desc1", "Spec1", 0.5);
        Phone p2 = new Phone(2L, 100.0, 10L, "Desc1", "Spec1", 0.5);
        Phone p3 = new Phone(3L, 100.0, 10L, "Desc1", "Spec1", 0.5);
        var phoneList = List.of(p1,p2,p3);
        return ResponseEntity.ok(phoneList);
    }

}
