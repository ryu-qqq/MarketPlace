package com.ryuqq.marketplace.domain.refund.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundException 테스트")
class RefundExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            RefundException exception = new RefundException(RefundErrorCode.REFUND_NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("환불 클레임을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("RFD-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            RefundException exception =
                    new RefundException(
                            RefundErrorCode.INVALID_STATUS_TRANSITION,
                            "REQUESTED 상태에서 COMPLETED 상태로 변경할 수 없습니다");

            // then
            assertThat(exception.getMessage()).isEqualTo("REQUESTED 상태에서 COMPLETED 상태로 변경할 수 없습니다");
            assertThat(exception.code()).isEqualTo("RFD-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            RefundException exception =
                    new RefundException(RefundErrorCode.REFUND_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("RFD-001");
        }

        @Test
        @DisplayName("보류 관련 예외를 생성한다")
        void createHoldException() {
            // when
            RefundException exception = new RefundException(RefundErrorCode.ALREADY_HOLD);

            // then
            assertThat(exception.getMessage()).isEqualTo("이미 보류 상태입니다");
            assertThat(exception.code()).isEqualTo("RFD-006");
            assertThat(exception.httpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("보류 사유 필수 예외를 생성한다")
        void createHoldReasonRequiredException() {
            // when
            RefundException exception = new RefundException(RefundErrorCode.HOLD_REASON_REQUIRED);

            // then
            assertThat(exception.getMessage()).isEqualTo("보류 사유는 필수입니다");
            assertThat(exception.code()).isEqualTo("RFD-004");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("RefundException은 DomainException을 상속한다")
        void refundExceptionExtendsDomainException() {
            // given
            RefundException exception = new RefundException(RefundErrorCode.REFUND_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("RefundException은 RuntimeException을 상속한다")
        void refundExceptionExtendsRuntimeException() {
            // given
            RefundException exception = new RefundException(RefundErrorCode.REFUND_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}
