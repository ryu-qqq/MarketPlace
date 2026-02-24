package com.ryuqq.marketplace.domain.shippingpolicy.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicyErrorCode 테스트")
class ShippingPolicyErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("SHIPPING_POLICY_NOT_FOUND 테스트")
    class ShippingPolicyNotFoundTest {

        @Test
        @DisplayName("에러 코드는 'SHP-001'이다")
        void hasCorrectCode() {
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND.getCode())
                    .isEqualTo("SHP-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void hasCorrectHttpStatus() {
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
        }

        @Test
        @DisplayName("메시지는 '배송 정책을 찾을 수 없습니다'이다")
        void hasCorrectMessage() {
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND.getMessage())
                    .isEqualTo("배송 정책을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("SHIPPING_POLICY_INACTIVE 테스트")
    class ShippingPolicyInactiveTest {

        @Test
        @DisplayName("에러 코드는 'SHP-002'이다")
        void hasCorrectCode() {
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_INACTIVE.getCode())
                    .isEqualTo("SHP-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_INACTIVE.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '비활성화된 배송 정책입니다'이다")
        void hasCorrectMessage() {
            assertThat(ShippingPolicyErrorCode.SHIPPING_POLICY_INACTIVE.getMessage())
                    .isEqualTo("비활성화된 배송 정책입니다");
        }
    }

    @Nested
    @DisplayName("INVALID_FREE_THRESHOLD 테스트")
    class InvalidFreeThresholdTest {

        @Test
        @DisplayName("에러 코드는 'SHP-005'이다")
        void hasCorrectCode() {
            assertThat(ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD.getCode())
                    .isEqualTo("SHP-005");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '무료배송 기준금액이 유효하지 않습니다'이다")
        void hasCorrectMessage() {
            assertThat(ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD.getMessage())
                    .isEqualTo("무료배송 기준금액이 유효하지 않습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ShippingPolicyErrorCode.values())
                    .containsExactly(
                            ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND,
                            ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND_FOR_SELLER,
                            ShippingPolicyErrorCode.SHIPPING_POLICY_INACTIVE,
                            ShippingPolicyErrorCode.SHIPPING_POLICY_ALREADY_DEFAULT,
                            ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD,
                            ShippingPolicyErrorCode.CANNOT_DEACTIVATE_DEFAULT_POLICY,
                            ShippingPolicyErrorCode.INACTIVE_POLICY_CANNOT_BE_DEFAULT,
                            ShippingPolicyErrorCode.CANNOT_UNMARK_ONLY_DEFAULT_POLICY,
                            ShippingPolicyErrorCode.LAST_ACTIVE_POLICY_CANNOT_BE_DEACTIVATED,
                            ShippingPolicyErrorCode.DEFAULT_SHIPPING_POLICY_NOT_FOUND);
        }
    }
}
