package com.glenneligio.phonestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;
    @Column(name = "brand_name", unique = true)
    private String name;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand", targetEntity = Phone.class)
    @JsonIgnore
    private List<Phone> phoneList;
}
