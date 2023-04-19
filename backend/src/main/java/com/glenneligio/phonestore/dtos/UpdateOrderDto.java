package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderDto {
    @NotBlank(message = "Order status must be present")
    @Pattern (regexp = "(COMPLETED|PENDING)", message = "Order status can only be PENDING or COMPLETED")
    private String status;
    @NotNull(message = "Order items must not be null")
    @NotEmpty(message = "Order items must not be empty")
    private List<OrderItemDto> orderItems;

    public static OrderEntity convertToEntity(UpdateOrderDto dto) {
        OrderEntity orderEntity = new OrderEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, orderEntity);

        List<OrderItemEntity> orderItemEntities = dto.getOrderItems().stream().map(OrderItemDto::convertToEntity).toList();
        orderEntity.setOrderItems(orderItemEntities);

        orderEntity.setStatus(OrderStatus.getOrderStatusType(dto.getStatus()));
        return orderEntity;
    }
}
