package com.springboot.work.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    @Id
    private String token;          // UUID

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    public VerificationToken(String email, long ttlHours) {
        this.token = UUID.randomUUID().toString();
        this.email = email;
        this.expiresAt = Instant.now().plusSeconds(ttlHours * 3600);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt) || used;
    }
}
