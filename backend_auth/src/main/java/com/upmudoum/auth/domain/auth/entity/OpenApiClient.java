package com.upmudoum.auth.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "open_api_clients")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenApiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String clientId;

    @Column(nullable = false, length = 64)
    private String clientSecretHash;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(nullable = false, length = 500)
    private String scopesCsv;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private Instant createdAt;

    public static String hashSecret(String clientSecret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(clientSecret.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hashed) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    public boolean matches(String rawClientSecret, String requestedSubject) {
        return enabled
                && subject.equals(requestedSubject)
                && clientSecretHash.equals(hashSecret(rawClientSecret));
    }

    public List<String> scopes() {
        if (scopesCsv == null || scopesCsv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(scopesCsv.split(","))
                .map(String::trim)
                .filter(scope -> !scope.isBlank())
                .toList();
    }
}
