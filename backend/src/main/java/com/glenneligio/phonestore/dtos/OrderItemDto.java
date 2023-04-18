package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.OrderEntity;
import com.glenneligio.phonestore.entity.OrderItemEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private Long id;
    private PhoneDto phone;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderItemEntity convertToEntity(OrderItemDto dto) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, orderItemEntity);

        PhoneEntity phoneEntity = PhoneDto.convertToEntity(dto.getPhone());
        orderItemEntity.setPhone(phoneEntity);
        return orderItemEntity;
    }

    public static OrderItemDto convertToDto(OrderItemEntity entity) {
        OrderItemDto orderItemDto = new OrderItemDto();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(entity, orderItemDto);

        PhoneDto phoneDto = PhoneDto.convertToDto(entity.getPhone());
        orderItemDto.setPhone(phoneDto);
        return orderItemDto;
    }
}
