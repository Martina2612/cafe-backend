package com.cafe.demo.security.jwt;

import com.cafe.demo.security.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final JwtProperties props;
    private Key hmacKey;

    public JwtService(JwtProperties props) {
        this.props = props;
    }

    @PostConstruct
    private void init() {
        String secret = props.getSecretKey();
        if (secret == null || secret.isBlank()) {
            // Para desarrollo: usamos un secreto por defecto pero avisamos en logs.
            // En producción: definí JWT_SECRET en tus env vars para evitar este fallback.
            log.warn("JWT secret not provided. Using insecure default secret for development. " +
                     "Set JWT_SECRET environment variable for production.");
            secret = "dev-secret-change-me-to-a-long-random-string-32chars+";
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // HS256 requiere una clave suficientemente larga. Fallamos rápido para evitar errores en runtime.
            throw new IllegalStateException("JWT secret is too short (must be at least 256 bits / 32 bytes). " +
                    "Provide a longer secret via environment variable JWT_SECRET.");
        }

        this.hmacKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSignInKey() {
        return hmacKey;
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + props.getExpirationMs());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to parse JWT or invalid token: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        if (extractedUsername == null) return false;
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}
