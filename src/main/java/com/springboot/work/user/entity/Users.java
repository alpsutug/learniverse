package com.springboot.work.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
    @SequenceGenerator(name = "id_seq", sequenceName = "id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "username",unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "age")
    private String age;

    @Column(name = "email")
    private String email;

    @Column(nullable = false)
    private boolean enabled = false;
}
