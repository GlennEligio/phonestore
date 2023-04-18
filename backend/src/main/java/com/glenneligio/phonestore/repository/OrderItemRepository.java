package com.glenneligio.phonestore.repository;

import com.glenneligio.phonestore.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
