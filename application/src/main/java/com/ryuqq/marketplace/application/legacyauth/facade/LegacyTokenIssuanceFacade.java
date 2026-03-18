package com.ryuqq.marketplace.application.legacyauth.facade;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenCacheCommandManager;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 레거시 토큰 발급 Facade.
 *
 * <p>토큰 발급 + 리프레시 토큰 캐시 저장을 묶어 처리합니다.
 * 트랜잭션은 걸지 않음 — 나중에 저장소가 Redis에서 DB로 변경될 때 트랜잭션 추가 예정.
 */
@Component
@ConditionalOnBean(LegacyTokenManager.class)
public class LegacyTokenIssuanceFacade {

    private final LegacyTokenManager tokenManager;
    private final LegacyTokenCacheCommandManager tokenCacheCommandManager;

    public LegacyTokenIssuanceFacade(
            LegacyTokenManager tokenManager,
            LegacyTokenCacheCommandManager tokenCacheCommandManager) {
        this.tokenManager = tokenManager;
        this.tokenCacheCommandManager = tokenCacheCommandManager;
    }

    /**
     * 토큰을 발급하고 리프레시 토큰을 캐시에 저장합니다.
     *
     * @param email subject (이메일)
     * @param sellerId 셀러 ID
     * @param roleType 역할
     * @return 액세스 토큰
     */
    public String issueAndCache(String email, long sellerId, String roleType) {
        LegacyTokenResult tokenResult = tokenManager.generateToken(email, sellerId, roleType);

        tokenCacheCommandManager.persist(
                tokenResult.email(),
                tokenResult.refreshToken(),
                tokenResult.expiresInSeconds());

        return tokenResult.accessToken();
    }
}
