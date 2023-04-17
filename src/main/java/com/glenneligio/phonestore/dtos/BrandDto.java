package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.BrandEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BrandDto convertToDto(BrandEntity brandEntity) {
        BrandDto dto = new BrandDto();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(brandEntity, dto);
        return dto;
    }

    public static BrandEntity convertToEntity(BrandDto dto) {
        BrandEntity entity = new BrandEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, entity);
        return entity;
    }
}
