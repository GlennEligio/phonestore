package com.glenneligio.phonestore.repository;

import com.glenneligio.phonestore.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {

    List<Phone> findByBrandName(String name);
}
