package com.glenneligio.phonestore.repository;

import com.glenneligio.phonestore.entity.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<PhoneEntity, Long> {

    List<PhoneEntity> findByBrandName(String name);
}
