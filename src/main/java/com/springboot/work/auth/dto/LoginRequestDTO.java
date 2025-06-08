package com.springboot.work.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email(message = "E-posta formatı hatalı")
    @NotBlank(message = "E-posta zorunludur")
    private String email;

    @NotBlank(message = "Şifre zorunludur")
    private String password;
}
