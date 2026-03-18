package com.ryuqq.marketplace.application.legacyauth.manager;

import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyTokenCacheQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 레거시 리프레시 토큰 캐시 조회 Manager. */
@Component
public class LegacyTokenCacheReadManager {

    private final LegacyTokenCacheQueryPort cacheQueryPort;

    public LegacyTokenCacheReadManager(LegacyTokenCacheQueryPort cacheQueryPort) {
        this.cacheQueryPort = cacheQueryPort;
    }

    public Optional<String> findByEmail(String email) {
        return cacheQueryPort.findByEmail(email);
    }
}
