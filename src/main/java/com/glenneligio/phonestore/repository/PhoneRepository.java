package com.glenneligio.phonestore.repository;

import com.glenneligio.phonestore.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
