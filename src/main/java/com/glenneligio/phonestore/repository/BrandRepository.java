package com.glenneligio.phonestore.repository;

import com.glenneligio.phonestore.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
