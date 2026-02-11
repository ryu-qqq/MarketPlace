package com.ryuqq.marketplace.integration.config;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.seller.port.out.client.IdentityClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminEmailClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminAuthOutboxCommandPort;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 통합 테스트용 외부 서비스 및 미구현 포트 Stub 설정.
 *
 * <p>AuthHub, SES 등 외부 서비스 클라이언트와 아직 Adapter가 구현되지 않은 포트 인터페이스를 Stub으로 대체합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@TestConfiguration
public class StubExternalClientConfig {

    // ===== 외부 서비스 클라이언트 Stubs =====

    @Bean
    @Primary
    public AuthClient stubAuthClient() {
        return new AuthClient() {
            private static final String VALID_IDENTIFIER = "admin@example.com";
            private static final String VALID_PASSWORD = "password123!";
            private static final String USER_ID = "test-user-001";
            private final AtomicLong tokenCounter = new AtomicLong(1);

            @Override
            public LoginResult login(String identifier, String password) {
                if (!VALID_IDENTIFIER.equals(identifier) || !VALID_PASSWORD.equals(password)) {
                    return LoginResult.failure("INVALID_CREDENTIALS", "Invalid credentials");
                }
                String token = "stub-access-token-" + tokenCounter.getAndIncrement();
                String refreshToken = "stub-refresh-token-" + tokenCounter.getAndIncrement();
                return LoginResult.success(USER_ID, token, refreshToken, 3600L, "Bearer");
            }

            @Override
            public void logout(String userId) {
                // stub: no-op
            }

            @Override
            public RefreshResult refresh(String refreshToken) {
                String newAccessToken = "stub-access-token-" + tokenCounter.getAndIncrement();
                String newRefreshToken = "stub-refresh-token-" + tokenCounter.getAndIncrement();
                return RefreshResult.success(newAccessToken, newRefreshToken, 3600L, "Bearer");
            }

            @Override
            public MyInfoResult getMyInfo(String accessToken) {
                return new MyInfoResult(
                        USER_ID,
                        VALID_IDENTIFIER,
                        "Test Admin",
                        "tenant-001",
                        "Test Tenant",
                        "org-admin-001",
                        "Test Organization",
                        List.of(new MyInfoResult.RoleInfo("role-001", "ROLE_SUPER_ADMIN")),
                        List.of("*:*"),
                        null,
                        null,
                        null);
            }
        };
    }

    @Bean
    @Primary
    public IdentityClient stubIdentityClient() {
        return outbox -> null;
    }

    @Bean
    @Primary
    public SellerAdminIdentityClient stubSellerAdminIdentityClient() {
        return new SellerAdminIdentityClient() {
            @Override
            public com.ryuqq.marketplace.application.selleradmin.dto.response
                            .SellerAdminIdentityProvisioningResult
                    provisionSellerAdminIdentity(SellerAdminAuthOutbox outbox) {
                return null;
            }

            @Override
            public void resetSellerAdminPassword(String authUserId) {
                // stub: no-op
            }

            @Override
            public void changeSellerAdminPassword(String authUserId, String newPassword) {
                // stub: no-op
            }
        };
    }

    @Bean
    @Primary
    public SellerAdminEmailClient stubSellerAdminEmailClient() {
        return outbox -> null;
    }

    // ===== 미구현 포트 Stubs =====

    @Bean
    @Primary
    public IdGeneratorPort stubIdGeneratorPort() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    @Primary
    public SellerAdminAuthOutboxCommandPort stubSellerAdminAuthOutboxCommandPort() {
        final AtomicLong sequence = new AtomicLong(1);
        return outbox -> sequence.getAndIncrement();
    }

    @Bean
    @Primary
    public SellerAdminAuthOutboxQueryPort stubSellerAdminAuthOutboxQueryPort() {
        return new SellerAdminAuthOutboxQueryPort() {
            @Override
            public Optional<SellerAdminAuthOutbox> findPendingBySellerAdminId(
                    SellerAdminId sellerAdminId) {
                return Optional.empty();
            }

            @Override
            public List<SellerAdminAuthOutbox> findPendingOutboxesForRetry(
                    Instant beforeTime, int limit) {
                return Collections.emptyList();
            }

            @Override
            public List<SellerAdminAuthOutbox> findProcessingTimeoutOutboxes(
                    Instant timeoutThreshold, int limit) {
                return Collections.emptyList();
            }
        };
    }
}
