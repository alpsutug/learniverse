package com.springboot.work.auth.repository;

import com.springboot.work.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, String> {

    void deleteByEmail(String email);   // tek seferlik token
}
