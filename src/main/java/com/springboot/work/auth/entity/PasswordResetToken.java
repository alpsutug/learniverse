package com.springboot.work.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class PasswordResetToken {

    @Id
    private String token;          // UUID string

    @Column(nullable = false)
    private String email;          // Kullanıcının maili

    @Column(nullable = false)
    private Instant expiresAt;     // Süre dolma

    public PasswordResetToken(String email, long ttlMinutes) {
        this.token = UUID.randomUUID().toString();
        this.email = email;
        this.expiresAt = Instant.now().plusSeconds(ttlMinutes * 60);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
