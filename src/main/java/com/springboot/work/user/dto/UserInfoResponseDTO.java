package com.springboot.work.user.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserInfoResponseDTO {

    private String name;

    private String surname;

    private String username;

    private int age;

    private String email;

    private double successRate; // ✅ yeni alan: 0.75 gibi

    private String level;

}
