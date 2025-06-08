package com.springboot.work.auth.service;

import com.springboot.work.auth.dto.*;

public interface AuthService {

    void register(RegisterRequestDTO request);
    JwtResponseDTO login(LoginRequestDTO request);

   void sendResetToken(ForgotPasswordRequestDTO request);
   void resetPassword(ResetPasswordRequestDTO request);
}
