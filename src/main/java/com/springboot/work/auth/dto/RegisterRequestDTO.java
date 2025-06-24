package com.springboot.work.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor              // JSON deserialization için
@AllArgsConstructor
public class RegisterRequestDTO {

    @Email(message = "E-posta formatı hatalı")
    @NotBlank(message = "E-posta zorunludur")
    private String email;

    @NotBlank(message = "Şifre zorunludur")
    @Size(min = 8, max = 64, message = "Şifre 8-64 karakter olmalı")
    // En az 1 büyük harf, 1 küçük, 1 rakam, 1 sembol
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,64}$",
            message = "Şifre en az 1 büyük, 1 küçük harf, 1 rakam ve 1 sembol içermeli"
    )
    private String password;
    private String username;
    private String name;
    private String surname;
    private Integer age;
}
