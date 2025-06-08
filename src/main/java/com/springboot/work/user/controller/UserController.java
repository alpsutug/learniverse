package com.springboot.work.user.controller;

import com.springboot.work.user.dto.UserRequestDTO;
import com.springboot.work.user.dto.UserResponseDTO;
import com.springboot.work.user.entity.Users;
import com.springboot.work.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @PostMapping(path = "/createUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseEntity<>(userService.createUser(userRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseDTO> deleteUser(@RequestParam Long id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }

    @GetMapping(path = "/getUserById")
    @ResponseStatus(HttpStatus.OK)
    public Users getUserById(@RequestParam Long id) {
        return userService.getUserById(id);
    }

    @PutMapping(path = "/updateUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserRequestDTO userRequestDTO, @RequestParam Long id) {
        return new ResponseEntity<>(userService.updateUser(userRequestDTO,id),HttpStatus.OK);
    }


    @GetMapping(path = "getAllList")
    @ResponseStatus(HttpStatus.OK)
    public List<Users> getAllList() {
        return userService.getAllList();
    }

    @GetMapping(path = "getUserByName")
    @ResponseStatus(HttpStatus.OK)
    public List<Users> getUserByName(@RequestParam String name) {
        return userService.getUserByName(name);

    }

    @GetMapping(path = "getUserByUserName")
    @ResponseStatus(HttpStatus.OK)
    public Users getUserByUserName(@RequestParam String username) {
        return userService.getUserByUserName(username);
    }


}
