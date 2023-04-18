package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fullName;
    private Boolean isActive;
    private UserType userType;

    public static UserDto convertToDto(UserEntity entity) {
        UserDto dto = new UserDto();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(entity, dto);
        return dto;
    }

    public static UserEntity convertToEntity(UserDto dto) {
        UserEntity entity = new UserEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, entity);
        return entity;
    }
}
