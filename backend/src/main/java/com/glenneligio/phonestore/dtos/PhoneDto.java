package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {
    private Long id;
    @Positive(message = "Price must be a positive number")
    private Double price;
    @Positive(message = "Quantity must be a positive number")
    private Long quantity;
    @NotBlank(message = "Description must be present")
    @Length(message = "Brand name can only have 64 characters", max = 256)
    private String description;
    @Length(message = "Brand name can only have 64 characters", max = 256)
    private String specification;
    @Max(message = "Discount can only have a max value of 1 for 100% discount", value = 1)
    @Min(message = "Discount can only have a min value of 0 for 0% discount", value = 0)
    private Double discount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Valid
    @NotNull(message = "Brand must be present")
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
