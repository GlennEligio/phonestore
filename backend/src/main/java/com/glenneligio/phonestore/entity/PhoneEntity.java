package com.glenneligio.phonestore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

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
    @Positive(message = "Price must be a positive number")
    private Double price;
    @Column(name = "quantity")
    @Positive(message = "Quantity must be a positive number")
    private Long quantity;
    @Column(name = "description")
    @Length(message = "Description can only have up to 256 characters")
    private String description;
    @Column(name = "specification")
    @Length(message = "Specification can only have up to 256 characters")
    private String specification;
    @Column(name = "discount")
    @Max(message = "Discount can only have a max value of 1 for 100% discount", value = 1)
    @Min(message = "Discount can only have a min value of 0 for 0% discount", value = 0)
    private Double discount;
    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = BrandEntity.class)
    @JoinColumn(name = "brand_name", referencedColumnName = "brand_name")
    @NotNull(message = "Brand must be present")
    private BrandEntity brand;
}
