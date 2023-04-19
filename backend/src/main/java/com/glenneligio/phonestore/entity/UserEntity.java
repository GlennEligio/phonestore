package com.glenneligio.phonestore.entity;

import com.glenneligio.phonestore.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Entity(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "username", unique = true)
    @NotBlank(message = "Username must be present")
    private String username;
    @Column(name = "password")
    @NotBlank(message = "Password must be present")
    private String password;
    @Column(name = "email")
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Email must be a valid one")
    private String email;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "full_name")
    @NotBlank(message = "Full name can't be blank")
    private String fullName;
    @Column(name = "is_active")
    @NotNull(message = "isActive flag must be present")
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    @NotNull(message = "User type must be present")
    private UserType userType;
@OneToMany(targetEntity = OrderEntity.class, mappedBy = "user")
    private List<OrderEntity> orderList;
}
