package com.ryuqq.marketplace.application.legacyauth.manager;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyTokenClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/** 레거시 토큰 발급/검증 Manager. */
@Component
@ConditionalOnBean(LegacyTokenClient.class)
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
}
