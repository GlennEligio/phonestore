package com.glenneligio.phonestore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    private Long id;
    private Double price;
    private Long quantity;
    private String description;
    private String specification;
    private Double discount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
