package com.ryuqq.marketplace.application.legacy.auth.manager;

import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenClient;
import org.springframework.stereotype.Component;

/** 레거시 토큰 발급/검증 Manager. */
@Component
public class LegacyTokenManager {

    private final LegacyTokenClient tokenClient;

    public LegacyTokenManager(LegacyTokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    public LegacyTokenResult generateToken(String email, long sellerId, String roleType) {
        return tokenClient.generateToken(email, sellerId, roleType);
    }

    public String extractSubject(String token) {
        return tokenClient.extractSubject(token);
    }

    public boolean isValid(String token) {
        return tokenClient.isValid(token);
    }

    public boolean isExpired(String token) {
        return tokenClient.isExpired(token);
    }

    public long extractSellerId(String token) {
        return tokenClient.extractSellerId(token);
    }

    public String extractRole(String token) {
        return tokenClient.extractRole(token);
    }
}
