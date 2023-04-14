package com.glenneligio.phonestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;
    @Column(name = "brand_name", unique = true)
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand", targetEntity = Phone.class)
    @JsonIgnore
    private List<Phone> phoneList;
}
