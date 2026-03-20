package com.ryuqq.marketplace.application.legacy.auth.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacySellerAuthCompositeReadManager;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyPasswordEncoder;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotApprovedException;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
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
@DisplayName("LegacySellerAuthValidator 테스트")
class LegacySellerAuthValidatorTest {

    @Mock private LegacySellerAuthCompositeReadManager sellerAuthReadManager;
    @Mock private LegacyPasswordEncoder passwordEncoder;

    @InjectMocks private LegacySellerAuthValidator validator;

    private static final String EMAIL = "seller@test.com";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedHash";

    @Nested
    @DisplayName("validateAndGet")
    class ValidateAndGetTest {

        @Test
        @DisplayName("유효한 셀러 - 승인됨, 비밀번호 일치")
        void validSeller_ReturnsResult() {
            LegacySellerAuthResult authResult =
                    new LegacySellerAuthResult(1L, EMAIL, ENCODED_PASSWORD, "SELLER", "APPROVED");
            given(sellerAuthReadManager.getByEmail(EMAIL)).willReturn(authResult);
            given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);

            LegacySellerAuthResult result = validator.validateAndGet(EMAIL, RAW_PASSWORD);

            assertThat(result.sellerId()).isEqualTo(1L);
            assertThat(result.email()).isEqualTo(EMAIL);
            assertThat(result.roleType()).isEqualTo("SELLER");
        }

        @Test
        @DisplayName("셀러 미발견 시 SellerAdminNotFoundException")
        void sellerNotFound_ThrowsException() {
            given(sellerAuthReadManager.getByEmail(EMAIL))
                    .willThrow(SellerAdminNotFoundException.withMessage("미발견"));

            assertThatThrownBy(() -> validator.validateAndGet(EMAIL, RAW_PASSWORD))
                    .isInstanceOf(SellerAdminNotFoundException.class);
        }

        @Test
        @DisplayName("미승인 셀러 시 SellerAdminNotApprovedException")
        void notApprovedSeller_ThrowsException() {
            LegacySellerAuthResult authResult =
                    new LegacySellerAuthResult(1L, EMAIL, ENCODED_PASSWORD, "SELLER", "PENDING");
            given(sellerAuthReadManager.getByEmail(EMAIL)).willReturn(authResult);

            assertThatThrownBy(() -> validator.validateAndGet(EMAIL, RAW_PASSWORD))
                    .isInstanceOf(SellerAdminNotApprovedException.class);
        }

        @Test
        @DisplayName("비밀번호 불일치 시 SellerAdminInvalidPasswordException")
        void invalidPassword_ThrowsException() {
            LegacySellerAuthResult authResult =
                    new LegacySellerAuthResult(1L, EMAIL, ENCODED_PASSWORD, "SELLER", "APPROVED");
            given(sellerAuthReadManager.getByEmail(EMAIL)).willReturn(authResult);
            given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(false);

            assertThatThrownBy(() -> validator.validateAndGet(EMAIL, RAW_PASSWORD))
                    .isInstanceOf(SellerAdminInvalidPasswordException.class);
        }
    }
}
