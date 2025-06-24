package com.springboot.work.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Token zorunludur")
    private String token;

    @NotBlank(message = "Yeni şifre zorunludur")
    @Size(min = 8, max = 64, message = "Şifre 8-64 karakter olmalı")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,64}$",
            message = "Şifre en az 1 büyük, 1 küçük harf, 1 rakam ve 1 sembol içermeli"
    )
    private String newPassword;
}
