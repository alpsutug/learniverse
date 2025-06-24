package com.springboot.work.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequestDTO {

    @Email(message = "E-posta formatı hatalı")
    @NotBlank(message = "E-posta zorunludur")
    private String email;
}
