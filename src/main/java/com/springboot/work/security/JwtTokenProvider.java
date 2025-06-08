package com.springboot.work.security;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;          // HMAC-SHA anahtarı
    private final long validityMs;  // Süre (ms)

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") Duration validityMs) {

        // env değişkeni 32+ karakter olsun – koda gömülü değil 🔐
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityMs = validityMs.toMillis();
    }

    /** Kullanıcıyı mail (subject) olarak yazar, süre ekler */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // süresi dolmuş, bozuk vs.
        }
    }
}
