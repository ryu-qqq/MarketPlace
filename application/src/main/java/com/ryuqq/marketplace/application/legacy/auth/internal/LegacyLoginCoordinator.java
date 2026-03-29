package com.ryuqq.marketplace.application.legacy.auth.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import com.ryuqq.marketplace.application.legacy.auth.facade.LegacyTokenIssuanceFacade;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import java.util.Base64;
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
        LoginResult authResult;
        try {
            authResult = authClient.login(identifier, password);
        } catch (Exception e) {
            throw new SellerAdminInvalidPasswordException();
        }

        if (authResult.isFailure()) {
            throw new SellerAdminInvalidPasswordException();
        }

        SellerAdmin sellerAdmin = sellerAdminReadManager.getByAuthUserId(authResult.userId());
        long sellerId = sellerAdmin.sellerIdValue();
        String roleType = resolveRoleType(authResult);

        return tokenIssuanceFacade.issueAndCache(identifier, sellerId, roleType);
    }

    private String resolveRoleType(LoginResult authResult) {
        try {
            String payload = authResult.accessToken().split("\\.")[1];
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            JsonNode claims = new ObjectMapper().readTree(decoded);
            JsonNode roles = claims.get("roles");
            if (roles != null && roles.isArray()) {
                for (JsonNode role : roles) {
                    if ("SUPER_ADMIN".equals(role.asText())) {
                        return "MASTER";
                    }
                }
            }
        } catch (Exception ignored) {
            // 파싱 실패 시 SELLER로 폴백
        }
        return "SELLER";
    }
}
