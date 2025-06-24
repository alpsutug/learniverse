package com.springboot.work.auth.controller;

import com.springboot.work.auth.dto.*;
import com.springboot.work.auth.entity.VerificationToken;
import com.springboot.work.auth.repository.VerificationTokenRepository;
import com.springboot.work.auth.service.AuthService;
import com.springboot.work.user.dto.UserResponseDTO;
import com.springboot.work.user.entity.Users;
import com.springboot.work.user.repository.UserRepository;
import com.springboot.work.util.WorkBusinessException;
import com.springboot.work.util.WorkMessageType;
import com.springboot.work.util.WorkMessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterRequestDTO req) {
        return new ResponseEntity<>(authService.register(req), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody @Valid LoginRequestDTO req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // şifre eski şifre ile aynı olamaz

    @PostMapping("/forgot-password")
    public ResponseEntity<UserResponseDTO> forgot(@RequestBody @Valid ForgotPasswordRequestDTO req) {
        return new ResponseEntity<>(authService.sendResetToken(req), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserResponseDTO> reset(@RequestBody @Valid ResetPasswordRequestDTO req) {
        return new ResponseEntity<>(authService.resetPassword(req), HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<UserResponseDTO> verify(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyAccount(token));
    }

}
