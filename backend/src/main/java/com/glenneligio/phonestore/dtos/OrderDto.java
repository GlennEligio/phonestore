package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;
    private List<OrderItemDto> orderItemsDto;

    public static OrderDto convertToDto(OrderEntity entity) {
        OrderDto orderDto = new OrderDto();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(entity, orderDto);
        List<OrderItemDto> orderItemDtos = entity.getOrderItems().stream().map(OrderItemDto::convertToDto).toList();
        orderDto.setOrderItemsDto(orderItemDtos);
        return orderDto;
    }

    public static OrderEntity convertToEntity(OrderDto dto) {
        OrderEntity orderEntity = new OrderEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, orderEntity);
        List<OrderItemEntity> orderItemEntities = dto.getOrderItemsDto().stream().map(OrderItemDto::convertToEntity).toList();
        orderEntity.setOrderItems(orderItemEntities);
        return orderEntity;
    }
}
