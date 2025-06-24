package com.springboot.work.auth.service;

import com.springboot.work.auth.dto.*;
import com.springboot.work.user.dto.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(RegisterRequestDTO request);
    JwtResponseDTO login(LoginRequestDTO request);
    UserResponseDTO sendResetToken(ForgotPasswordRequestDTO request);
    UserResponseDTO resetPassword(ResetPasswordRequestDTO request);
    UserResponseDTO verifyAccount(String token);
}
