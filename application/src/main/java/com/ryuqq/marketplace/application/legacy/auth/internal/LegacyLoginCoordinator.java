package com.ryuqq.marketplace.application.legacy.auth.internal;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import com.ryuqq.marketplace.application.legacy.auth.facade.LegacyTokenIssuanceFacade;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import org.springframework.stereotype.Component;

/**
 * 레거시 로그인 Coordinator.
 *
 * <p>AuthHub로 인증 후 자체 JWT를 발급합니다. luxurydb 직접 조회 없이 AuthHub + market 스키마만 사용합니다.
 */
@Component
public class LegacyLoginCoordinator {

    private final AuthClient authClient;
    private final SellerAdminReadManager sellerAdminReadManager;
    private final LegacyTokenIssuanceFacade tokenIssuanceFacade;

    public LegacyLoginCoordinator(
            AuthClient authClient,
            SellerAdminReadManager sellerAdminReadManager,
            LegacyTokenIssuanceFacade tokenIssuanceFacade) {
        this.authClient = authClient;
        this.sellerAdminReadManager = sellerAdminReadManager;
        this.tokenIssuanceFacade = tokenIssuanceFacade;
    }

    /**
     * 레거시 로그인을 수행합니다.
     *
     * <p>AuthHub에서 비밀번호 검증 → userId로 market.seller_admins 조회 → 자체 JWT 발급.
     *
     * @param identifier 이메일
     * @param password 평문 비밀번호
     * @return 자체 액세스 토큰
     */
    public String login(String identifier, String password) {
        LoginResult authResult = authClient.login(identifier, password);
        if (authResult.isFailure()) {
            throw new SellerAdminInvalidPasswordException();
        }

        SellerAdmin sellerAdmin = sellerAdminReadManager.getByAuthUserId(authResult.userId());
        long sellerId = sellerAdmin.sellerIdValue();
        String roleType = resolveRoleType(authResult);

        return tokenIssuanceFacade.issueAndCache(identifier, sellerId, roleType);
    }

    private String resolveRoleType(LoginResult authResult) {
        // AuthHub의 getMyInfo로 role 확인 필요 — LoginResult에는 role이 없음
        // userId로 market.seller_admins에서 판단: sellerId == 마스터 조직이면 MASTER
        // 간단 방식: AuthClient.getMyInfo()로 role 조회
        var myInfo = authClient.getMyInfo(authResult.userId());
        if (myInfo.roles() != null) {
            boolean isSuperAdmin =
                    myInfo.roles().stream().anyMatch(r -> "SUPER_ADMIN".equals(r.name()));
            if (isSuperAdmin) {
                return "MASTER";
            }
        }
        return "SELLER";
    }
}
