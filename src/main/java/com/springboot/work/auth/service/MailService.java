package com.springboot.work.auth.service;

public interface MailService {
    /**
     * Şifre sıfırlama tokenını e-postayla gönderir.
     *
     * @param to    alıcı e-posta adresi
     * @param token tek kullanımlık sıfırlama kodu
     */
    void sendResetPasswordMail(String to, String token);
    // mail/service/MailService.java
    void sendActivationMail(String to, String token);

}
