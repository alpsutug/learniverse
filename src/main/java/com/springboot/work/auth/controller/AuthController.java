package com.springboot.work.auth.controller;

import com.springboot.work.auth.dto.*;
import com.springboot.work.auth.entity.VerificationToken;
import com.springboot.work.auth.repository.VerificationTokenRepository;
import com.springboot.work.auth.service.AuthService;
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
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository usersRepository;
    private final WorkMessageUtil msgUtil;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody @Valid LoginRequestDTO req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // şifre eski şifre ile aynı olamaz



    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgot(@RequestBody @Valid ForgotPasswordRequestDTO req) {
        authService.sendResetToken(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> reset(@RequestBody @Valid ResetPasswordRequestDTO req) {
        authService.resetPassword(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {

        VerificationToken vt = verificationTokenRepository.findById(token)
                .orElseThrow(() -> new WorkBusinessException(
                        msgUtil.createWorkMessageWithCode("dogrulama.token.gecersiz", WorkMessageType.ERROR)));

        if (vt.isExpired()) {
            verificationTokenRepository.delete(vt);
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("dogrulama.token.suresi.doldu", WorkMessageType.ERROR));
        }

        Users user = usersRepository.findByEmail(vt.getEmail());   // ✔ doğru repo ismi
        if (user == null) {
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("kullanici.bulunamadi", WorkMessageType.ERROR));
        }

        user.setEnabled(true);
        usersRepository.save(user);

        vt.setUsed(true);               // isteğe bağlı: delete(vt) yapabilirsin
        verificationTokenRepository.save(vt);

        return ResponseEntity.ok("Hesabınız aktifleştirildi, giriş yapabilirsiniz.");
    }



}
