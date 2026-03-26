package com.ryuqq.marketplace.application.legacy.auth.internal;

import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.facade.LegacyTokenIssuanceFacade;
import com.ryuqq.marketplace.application.legacy.auth.validator.LegacySellerAuthValidator;
import org.springframework.stereotype.Component;

/**
 * 레거시 로그인 Coordinator.
 *
 * <p>검증 → 토큰 발급을 오케스트레이션합니다.
 */
@Component
public class LegacyLoginCoordinator {

    private final LegacySellerAuthValidator sellerAuthValidator;
    private final LegacyTokenIssuanceFacade tokenIssuanceFacade;

    public LegacyLoginCoordinator(
            LegacySellerAuthValidator sellerAuthValidator,
            LegacyTokenIssuanceFacade tokenIssuanceFacade) {
        this.sellerAuthValidator = sellerAuthValidator;
        this.tokenIssuanceFacade = tokenIssuanceFacade;
    }

    /**
     * 레거시 로그인을 수행합니다.
     *
     * @param identifier 이메일
     * @param password 평문 비밀번호
     * @return 액세스 토큰
     */
    public String login(String identifier, String password) {
        LegacySellerAuthResult authResult =
                sellerAuthValidator.validateAndGet(identifier, password);

        return tokenIssuanceFacade.issueAndCache(
                authResult.email(), authResult.sellerId(), authResult.roleType());
    }
}
