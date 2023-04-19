package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    @NotNull(message = "Order items must not be null")
    @NotEmpty(message = "Order items must not be empty")
    private List<OrderItemDto> orderItems;


    public static OrderEntity convertToEntity(CreateOrderDto dto) {
        OrderEntity orderEntity = new OrderEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, orderEntity);

        List<OrderItemEntity> orderItemEntities = dto.getOrderItems().stream().map(OrderItemDto::convertToEntity).toList();
        orderEntity.setOrderItems(orderItemEntities);

        return orderEntity;
    }
}

