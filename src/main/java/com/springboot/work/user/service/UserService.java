package com.springboot.work.user.service;


import com.springboot.work.user.dto.UserInfoResponseDTO;
import com.springboot.work.user.dto.UserRequestDTO;
import com.springboot.work.user.dto.UserResponseDTO;
import com.springboot.work.user.entity.Users;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO deleteUser(Long id);
    Users getUserById(Long id);
    UserResponseDTO updateUser(UserRequestDTO userRequestDTO,Long id);
    List<Users> getAllList();
    List<Users> getUserByName(String name);
    Users getUserByUserName(String username);
    UserInfoResponseDTO getUserInfo(String email);


}
