package com.ryuqq.marketplace.application.legacyauth.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacyauth.facade.LegacyTokenIssuanceFacade;
import com.ryuqq.marketplace.application.legacyauth.validator.LegacySellerAuthValidator;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyLoginCoordinator 테스트")
class LegacyLoginCoordinatorTest {

    @Mock private LegacySellerAuthValidator sellerAuthValidator;
    @Mock private LegacyTokenIssuanceFacade tokenIssuanceFacade;

    @InjectMocks private LegacyLoginCoordinator coordinator;

    private static final String EMAIL = "seller@test.com";
    private static final String PASSWORD = "password123";
    private static final String ACCESS_TOKEN = "access.jwt.token";

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 - 검증 통과 후 토큰 발급")
        void login_Success() {
            LegacySellerAuthResult authResult =
                    new LegacySellerAuthResult(1L, EMAIL, "hash", "SELLER", "APPROVED");
            given(sellerAuthValidator.validateAndGet(EMAIL, PASSWORD)).willReturn(authResult);
            given(tokenIssuanceFacade.issueAndCache(EMAIL, 1L, "SELLER"))
                    .willReturn(ACCESS_TOKEN);

            String result = coordinator.login(EMAIL, PASSWORD);

            assertThat(result).isEqualTo(ACCESS_TOKEN);
            verify(sellerAuthValidator).validateAndGet(EMAIL, PASSWORD);
            verify(tokenIssuanceFacade).issueAndCache(EMAIL, 1L, "SELLER");
        }

        @Test
        @DisplayName("검증 실패 시 토큰 발급 안 함")
        void login_ValidationFailed_NoTokenIssued() {
            given(sellerAuthValidator.validateAndGet(EMAIL, PASSWORD))
                    .willThrow(new SellerAdminInvalidPasswordException());

            assertThatThrownBy(() -> coordinator.login(EMAIL, PASSWORD))
                    .isInstanceOf(SellerAdminInvalidPasswordException.class);
        }
    }
}
