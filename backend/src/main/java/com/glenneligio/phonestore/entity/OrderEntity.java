package com.glenneligio.phonestore.entity;

import com.glenneligio.phonestore.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @NotNull(message = "Order status must be present")
    private OrderStatus status;
    @OneToMany(fetch = FetchType.EAGER,
            targetEntity = OrderItemEntity.class,
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            mappedBy = "order")
    @NotNull(message = "Order items must be present")
    private List<OrderItemEntity> orderItems;
    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @NotNull(message = "User must be present")
    private UserEntity user;
}
