package com.springboot.work.auth.service;

public interface MailService {

    void sendResetPasswordMail(String to, String token);
    void sendActivationMail(String to, String token);

}
