package com.springboot.work.auth.service.impl;

import com.springboot.work.auth.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendResetPasswordMail(String to, String token) {
        String link = "https://frontend.app/reset?token=" + token;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Şifre Sıfırlama İsteği");
        msg.setText(String.format("""
                Merhaba,

                Şifrenizi sıfırlamak için aşağıdaki bağlantıya %d dakika içinde tıklayın:
                %s

                Eğer bu isteği siz yapmadıysanız lütfen dikkate almayın.
                """, 15, link));

        mailSender.send(msg);
    }


    // mail/service/impl/MailServiceImpl.java
    @Override
    public void sendActivationMail(String to, String token) {
        String link = "http://localhost:8010/auth/verify?token=" + token;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Hesabınızı Aktifleştirin");
        msg.setText("""
        Merhaba,

        Work hesabınızı aktifleştirmek için aşağıdaki bağlantıya 24 saat içinde tıklayın:
        %s

        Eğer bu kayıt size ait değilse lütfen bu mesajı yok sayın.
        """.formatted(link));

        mailSender.send(msg);
    }

}
