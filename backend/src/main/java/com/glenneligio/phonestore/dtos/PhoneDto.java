package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {
    private Long id;
    private Double price;
    private Long quantity;
    private String description;
    private String specification;
    private Double discount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BrandDto brand;

    public static PhoneDto convertToDto(PhoneEntity entity) {
        PhoneDto dto = new PhoneDto();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(entity, dto);

        BrandDto brandDto = BrandDto.convertToDto(entity.getBrand());
        dto.setBrand(brandDto);
        return dto;
    }

    public static PhoneEntity convertToEntity(PhoneDto dto) {
        PhoneEntity entity = new PhoneEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, entity);

        BrandEntity brandEntity = BrandDto.convertToEntity(dto.getBrand());
        entity.setBrand(brandEntity);
        return entity;
    }
}
