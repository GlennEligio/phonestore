package com.glenneligio.phonestore.dtos;

import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "Username can't be blank")
    private String username;
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Email must be a valid one")
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotBlank(message = "Full name can't be blank")
    private String fullName;
    @NotNull(message = "isActive flag must be present")
    private Boolean isActive;
    @NotBlank(message = "User type must be present")
    @Pattern(regexp = "(CUSTOMER|ADMIN)", message = "User can only be of type CUSTOMER or ADMIN")
    private String userType;

    public static UserDto convertToDto(UserEntity entity) {
        UserDto dto = new UserDto();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(entity, dto);

        dto.setUserType(entity.getUserType().getType());
        return dto;
    }

    public static UserEntity convertToEntity(UserDto dto) {
        UserEntity entity = new UserEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, entity);

        entity.setUserType(UserType.getAccountType(dto.getUserType()));
        return entity;
    }
}
