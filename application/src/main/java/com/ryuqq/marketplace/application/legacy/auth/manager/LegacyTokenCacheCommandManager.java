package com.ryuqq.marketplace.application.legacy.auth.manager;

import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheCommandPort;
import org.springframework.stereotype.Component;

/** 레거시 리프레시 토큰 캐시 저장 Manager. */
@Component
public class LegacyTokenCacheCommandManager {

    private final LegacyTokenCacheCommandPort cacheCommandPort;

    public LegacyTokenCacheCommandManager(LegacyTokenCacheCommandPort cacheCommandPort) {
        this.cacheCommandPort = cacheCommandPort;
    }

    public void persist(String email, String refreshToken, long expiresInSeconds) {
        cacheCommandPort.persist(email, refreshToken, expiresInSeconds);
    }
}
