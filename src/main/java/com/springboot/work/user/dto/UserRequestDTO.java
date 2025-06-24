package com.springboot.work.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserRequestDTO {
    private String name;
    private String surname;
    private String username;
    private String password;
    private String email;
    private Integer age;


}
