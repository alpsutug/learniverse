package com.springboot.work.auth.repository;

import com.springboot.work.auth.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, String> {
    void deleteByEmail(String email);
}
