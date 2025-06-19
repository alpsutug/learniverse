package com.springboot.work.user.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserInfoResponseDTO {

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "username",unique = true)
    private String username;

    @Column(name = "age")
    private String age;

    @Column(name = "email")
    private String email;

    private double successRate; // ✅ yeni alan: 0.75 gibi

}
