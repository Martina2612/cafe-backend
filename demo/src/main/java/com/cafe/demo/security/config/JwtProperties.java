package com.cafe.demo.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {
    /**
     * Secret key used to sign the JWT. Provided via environment variable JWT_SECRET.
     * In application.properties: application.security.jwt.secretKey=${JWT_SECRET:}
     */
    private String secretKey;

    /**
     * Expiration time in milliseconds. Default 1 hour.
     * In application.properties: application.security.jwt.expirationMs=${JWT_EXPIRATION_MS:3600000}
     */
    private long expirationMs = 3600_000L;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
