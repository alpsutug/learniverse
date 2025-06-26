package com.springboot.work.auth.service.impl;

import com.springboot.work.auth.dto.*;
import com.springboot.work.auth.entity.PasswordResetToken;
import com.springboot.work.auth.entity.VerificationToken;
import com.springboot.work.auth.repository.PasswordResetTokenRepository;
import com.springboot.work.auth.repository.VerificationTokenRepository;
import com.springboot.work.auth.service.AuthService;
import com.springboot.work.security.JwtTokenProvider;
import com.springboot.work.user.dto.UserResponseDTO;
import com.springboot.work.user.entity.Users;
import com.springboot.work.user.repository.UserRepository;
import com.springboot.work.util.WorkBusinessException;
import com.springboot.work.util.WorkMessageDTO;
import com.springboot.work.util.WorkMessageType;
import com.springboot.work.util.WorkMessageUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.springboot.work.util.WorkConstant.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;
    private final WorkMessageUtil msgUtil;   // Senin mevcut mesaj sistemin
    private final PasswordResetTokenRepository tokenRepo;
    private final MailServiceImpl mailService;

    private static final long TOKEN_TTL_MIN = 15;
    private static final long VERIFY_TTL_H   = 24;
    private final VerificationTokenRepository verificationTokenRepository;

    /* ---------- REGISTER ---------- */
    @Override
    @Transactional
    public UserResponseDTO register(@Valid RegisterRequestDTO req) {


        if (!StringUtils.hasText(req.getEmail()) || !StringUtils.hasText(req.getPassword())) {
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("gonderilen.istek.hatali", WorkMessageType.ERROR));
        }

        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("email.zaten.kayitli", WorkMessageType.ERROR));
        }

        /* — Kullanıcı pasif olarak kaydediliyor — */
        Users u = new Users();
        u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setUsername(req.getUsername());
        u.setName(req.getName());
        u.setAge(req.getAge());
        u.setSurname(req.getSurname());
        u.setEnabled(false);                       // 🔑 aktivasyon bekliyor
        userRepository.save(u);

        /* — Eski doğrulama token’ı sil, yenisini oluştur — */
        verificationTokenRepository.deleteByEmail(u.getEmail());

        VerificationToken vToken = new VerificationToken(u.getEmail(), VERIFY_TTL_H);
        verificationTokenRepository.save(vToken);

        /* — Aktivasyon e-postası gönder — */
        mailService.sendActivationMail(u.getEmail(), vToken.getToken());

        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text(USER_SAVED_SUCCESS)
                .build();

        return UserResponseDTO.builder()
                .userId(u.getId())
                .msg(List.of(successMessage))
                .build();
    }

    /* ---------- LOGIN ---------- */
    @Override
    public JwtResponseDTO login(@Valid LoginRequestDTO req) {

        Users user = userRepository.findByEmail(req.getEmail());
        if (user == null || !encoder.matches(req.getPassword(), user.getPassword())) {
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("girilen.bilgiler.hatali", WorkMessageType.ERROR));
        }
        if (!user.isEnabled()) {
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("hesap.aktif.degil", WorkMessageType.ERROR));
        }
        boolean isActive = user.isEnabled();
        String token = jwt.generateToken(user.getEmail());
        return new JwtResponseDTO(token,isActive);
    }

    /* ---------- Şifre sıfırlama iskeletleri ---------- */


    /* ---------- FORGOT PASSWORD ---------- */
    @Override
    @Transactional
    public UserResponseDTO sendResetToken(ForgotPasswordRequestDTO req) {
        Users user = userRepository.findByEmail(req.getEmail());
        if (user == null) {
            // Bilinçli olarak HATA vermiyoruz: kullanıcı var/yok bilgisini sızdırma
            return UserResponseDTO.builder().build();
        }
        PasswordResetToken token = new PasswordResetToken(user.getEmail(), TOKEN_TTL_MIN);

        // Önce maili gönder — başarısız olursa zaten aşağıya geçmeyecek
        mailService.sendResetPasswordMail(user.getEmail(), token.getToken());

        // Eski token'ı sil
        tokenRepo.deleteByEmail(user.getEmail());

        // Şimdi token'ı DB'ye kaydet
        tokenRepo.save(token);

        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text(FORGOT_PASSWORD)
                .build();

        return UserResponseDTO.builder()
                .userId(user.getId())
                .msg(List.of(successMessage))
                .build();
    }

    /* ---------- RESET PASSWORD ---------- */
    @Override
    @Transactional
    public UserResponseDTO resetPassword(ResetPasswordRequestDTO req) {
        PasswordResetToken token = tokenRepo.findById(req.getToken())
                .orElseThrow(() -> new WorkBusinessException(
                        msgUtil.createWorkMessageWithCode("token.gecersiz", WorkMessageType.ERROR)));

        if (token.isExpired()) {
            tokenRepo.delete(token);
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("token.suresi.doldu", WorkMessageType.ERROR));
        }

        Users user = userRepository.findByEmail(token.getEmail());
        if (user == null) {             // teorik olarak olmamalı
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode("kullanici.bulunamadi", WorkMessageType.ERROR));
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepository.save(user);
        tokenRepo.delete(token);

        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text(RESET_PASSWORD)
                .build();

        return UserResponseDTO.builder()
                .userId(user.getId())
                .msg(List.of(successMessage))
                .build();// tek kullanım

    }


    @Override
    @Transactional
    public UserResponseDTO verifyAccount(String token) {

        VerificationToken vt = verificationTokenRepository.findById(token)
                .orElseThrow(() -> new WorkBusinessException(
                        msgUtil.createWorkMessageWithCode(
                                "dogrulama.token.gecersiz", WorkMessageType.ERROR)));

        if (vt.isExpired()) {
            verificationTokenRepository.delete(vt);
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode(
                            "dogrulama.token.suresi.doldu", WorkMessageType.ERROR));
        }

        Users user = userRepository.findByEmail(vt.getEmail());
        if (user == null) {
            throw new WorkBusinessException(
                    msgUtil.createWorkMessageWithCode(
                            "kullanici.bulunamadi", WorkMessageType.ERROR));
        }

        user.setEnabled(true);
        userRepository.save(user);

        vt.setUsed(true);                      // veya verificationTokenRepository.delete(vt);
        verificationTokenRepository.save(vt);

        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text(VERIFY_ACCOUNT)
                .build();

        return UserResponseDTO.builder()
                .userId(user.getId())
                .msg(List.of(successMessage))
                .build();// tek kullanım
    }
}

