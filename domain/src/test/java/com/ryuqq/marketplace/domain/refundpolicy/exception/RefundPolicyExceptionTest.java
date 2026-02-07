package com.ryuqq.marketplace.domain.refundpolicy.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicyException 테스트")
class RefundPolicyExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            RefundPolicyException exception =
                    new RefundPolicyException(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("환불 정책을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("RFP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            RefundPolicyException exception =
                    new RefundPolicyException(
                            RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND, "ID 456 환불 정책 없음");

            // then
            assertThat(exception.getMessage()).isEqualTo("ID 456 환불 정책 없음");
            assertThat(exception.code()).isEqualTo("RFP-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            RefundPolicyException exception =
                    new RefundPolicyException(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("RFP-001");
        }
    }

    @Nested
    @DisplayName("정적 팩토리 메서드 테스트")
    class StaticFactoryMethodTest {

        @Test
        @DisplayName("policyNotFound()로 예외를 생성한다")
        void createWithPolicyNotFound() {
            // when
            RefundPolicyException exception = RefundPolicyException.policyNotFound();

            // then
            assertThat(exception.code()).isEqualTo("RFP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("환불 정책을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("policyInactive()로 예외를 생성한다")
        void createWithPolicyInactive() {
            // when
            RefundPolicyException exception = RefundPolicyException.policyInactive();

            // then
            assertThat(exception.code()).isEqualTo("RFP-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("비활성화된 환불 정책입니다");
        }

        @Test
        @DisplayName("returnPeriodExpired()로 예외를 생성한다")
        void createWithReturnPeriodExpired() {
            // when
            RefundPolicyException exception = RefundPolicyException.returnPeriodExpired();

            // then
            assertThat(exception.code()).isEqualTo("RFP-007");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("반품 가능 기간이 만료되었습니다");
        }

        @Test
        @DisplayName("exchangePeriodExpired()로 예외를 생성한다")
        void createWithExchangePeriodExpired() {
            // when
            RefundPolicyException exception = RefundPolicyException.exchangePeriodExpired();

            // then
            assertThat(exception.code()).isEqualTo("RFP-008");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("교환 가능 기간이 만료되었습니다");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void extendsDomainException() {
            // given
            RefundPolicyException exception = RefundPolicyException.policyNotFound();

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
