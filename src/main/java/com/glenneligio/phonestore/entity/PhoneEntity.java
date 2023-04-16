package com.glenneligio.phonestore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "phones")
public class PhoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id")
    private Long id;
    @Column(name = "price")
    private Double price;
    @Column(name = "quantity")
    private Long quantity;
    @Column(name = "description")
    private String description;
    @Column(name = "specification")
    private String specification;
    @Column(name = "discount")
    private Double discount;
    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = BrandEntity.class)
    @JoinColumn(name = "brand_name", referencedColumnName = "brand_name")
    private BrandEntity brand;
}
