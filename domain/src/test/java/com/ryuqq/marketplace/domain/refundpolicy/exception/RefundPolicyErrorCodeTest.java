package com.ryuqq.marketplace.domain.refundpolicy.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicyErrorCode 테스트")
class RefundPolicyErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("REFUND_POLICY_NOT_FOUND 테스트")
    class RefundPolicyNotFoundTest {

        @Test
        @DisplayName("에러 코드는 'RFP-001'이다")
        void hasCorrectCode() {
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND.getCode())
                    .isEqualTo("RFP-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void hasCorrectHttpStatus() {
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
        }

        @Test
        @DisplayName("메시지는 '환불 정책을 찾을 수 없습니다'이다")
        void hasCorrectMessage() {
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND.getMessage())
                    .isEqualTo("환불 정책을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("REFUND_POLICY_INACTIVE 테스트")
    class RefundPolicyInactiveTest {

        @Test
        @DisplayName("에러 코드는 'RFP-002'이다")
        void hasCorrectCode() {
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_INACTIVE.getCode()).isEqualTo("RFP-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_INACTIVE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '비활성화된 환불 정책입니다'이다")
        void hasCorrectMessage() {
            assertThat(RefundPolicyErrorCode.REFUND_POLICY_INACTIVE.getMessage())
                    .isEqualTo("비활성화된 환불 정책입니다");
        }
    }

    @Nested
    @DisplayName("RETURN_PERIOD_EXPIRED 테스트")
    class ReturnPeriodExpiredTest {

        @Test
        @DisplayName("에러 코드는 'RFP-007'이다")
        void hasCorrectCode() {
            assertThat(RefundPolicyErrorCode.RETURN_PERIOD_EXPIRED.getCode()).isEqualTo("RFP-007");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(RefundPolicyErrorCode.RETURN_PERIOD_EXPIRED.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '반품 가능 기간이 만료되었습니다'이다")
        void hasCorrectMessage() {
            assertThat(RefundPolicyErrorCode.RETURN_PERIOD_EXPIRED.getMessage())
                    .isEqualTo("반품 가능 기간이 만료되었습니다");
        }
    }

    @Nested
    @DisplayName("EXCHANGE_PERIOD_EXPIRED 테스트")
    class ExchangePeriodExpiredTest {

        @Test
        @DisplayName("에러 코드는 'RFP-008'이다")
        void hasCorrectCode() {
            assertThat(RefundPolicyErrorCode.EXCHANGE_PERIOD_EXPIRED.getCode())
                    .isEqualTo("RFP-008");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(RefundPolicyErrorCode.EXCHANGE_PERIOD_EXPIRED.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '교환 가능 기간이 만료되었습니다'이다")
        void hasCorrectMessage() {
            assertThat(RefundPolicyErrorCode.EXCHANGE_PERIOD_EXPIRED.getMessage())
                    .isEqualTo("교환 가능 기간이 만료되었습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(RefundPolicyErrorCode.values())
                    .containsExactly(
                            RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND,
                            RefundPolicyErrorCode.REFUND_POLICY_INACTIVE,
                            RefundPolicyErrorCode.REFUND_POLICY_ALREADY_DEFAULT,
                            RefundPolicyErrorCode.INVALID_RETURN_PERIOD,
                            RefundPolicyErrorCode.INVALID_EXCHANGE_PERIOD,
                            RefundPolicyErrorCode.RETURN_PERIOD_EXPIRED,
                            RefundPolicyErrorCode.EXCHANGE_PERIOD_EXPIRED,
                            RefundPolicyErrorCode.CANNOT_DEACTIVATE_DEFAULT_POLICY,
                            RefundPolicyErrorCode.INACTIVE_POLICY_CANNOT_BE_DEFAULT,
                            RefundPolicyErrorCode.CANNOT_UNMARK_ONLY_DEFAULT_POLICY,
                            RefundPolicyErrorCode.LAST_ACTIVE_POLICY_CANNOT_BE_DEACTIVATED);
        }
    }
}
