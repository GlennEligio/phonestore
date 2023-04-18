package com.glenneligio.phonestore.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    @NotBlank(message = "Login username can not be blank")
    private String username;
    @NotBlank(message = "Login password can not be blank")
    private String password;
}
