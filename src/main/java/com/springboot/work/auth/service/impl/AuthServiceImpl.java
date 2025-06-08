package com.springboot.work.auth.service.impl;

import com.springboot.work.auth.dto.*;
import com.springboot.work.auth.entity.PasswordResetToken;
import com.springboot.work.auth.entity.VerificationToken;
import com.springboot.work.auth.repository.PasswordResetTokenRepository;
import com.springboot.work.auth.repository.VerificationTokenRepository;
import com.springboot.work.auth.service.AuthService;
import com.springboot.work.security.JwtTokenProvider;
import com.springboot.work.user.entity.Users;
import com.springboot.work.user.repository.UserRepository;
import com.springboot.work.util.WorkBusinessException;
import com.springboot.work.util.WorkMessageType;
import com.springboot.work.util.WorkMessageUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public void register(@Valid RegisterRequestDTO req) {


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
        u.setSurname(req.getSurname());
        u.setEnabled(false);                       // 🔑 aktivasyon bekliyor
        userRepository.save(u);

        /* — Eski doğrulama token’ı sil, yenisini oluştur — */
        verificationTokenRepository.deleteByEmail(u.getEmail());

        VerificationToken vToken = new VerificationToken(u.getEmail(), VERIFY_TTL_H);
        verificationTokenRepository.save(vToken);

        /* — Aktivasyon e-postası gönder — */
        mailService.sendActivationMail(u.getEmail(), vToken.getToken());
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
        String token = jwt.generateToken(user.getEmail());
        return new JwtResponseDTO(token);
    }

    /* ---------- Şifre sıfırlama iskeletleri ---------- */


    /* ---------- FORGOT PASSWORD ---------- */
    @Override
    @Transactional
    public void sendResetToken(ForgotPasswordRequestDTO req) {
        Users user = userRepository.findByEmail(req.getEmail());
        if (user == null) {
            // Bilinçli olarak HATA vermiyoruz: kullanıcı var/yok bilgisini sızdırma
            return;
        }
        PasswordResetToken token = new PasswordResetToken(user.getEmail(), TOKEN_TTL_MIN);

        // Önce maili gönder — başarısız olursa zaten aşağıya geçmeyecek
        mailService.sendResetPasswordMail(user.getEmail(), token.getToken());

        // Eski token'ı sil
        tokenRepo.deleteByEmail(user.getEmail());

        // Şimdi token'ı DB'ye kaydet
        tokenRepo.save(token);
    }

    /* ---------- RESET PASSWORD ---------- */
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO req) {
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
        tokenRepo.delete(token);        // tek kullanım
    }
}

