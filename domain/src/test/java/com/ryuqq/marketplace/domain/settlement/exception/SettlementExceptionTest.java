package com.ryuqq.marketplace.domain.settlement.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Settlement 예외 테스트")
class SettlementExceptionTest {

    @Nested
    @DisplayName("SettlementErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("SETTLEMENT_NOT_FOUND 에러 코드가 올바르다")
        void settlementNotFound() {
            SettlementErrorCode code = SettlementErrorCode.SETTLEMENT_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("STL-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드가 올바르다")
        void invalidStatusTransition() {
            SettlementErrorCode code = SettlementErrorCode.INVALID_STATUS_TRANSITION;
            assertThat(code.getCode()).isEqualTo("STL-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("상태 변경");
        }

        @Test
        @DisplayName("NOT_PENDING_STATUS 에러 코드가 올바르다")
        void notPendingStatus() {
            SettlementErrorCode code = SettlementErrorCode.NOT_PENDING_STATUS;
            assertThat(code.getCode()).isEqualTo("STL-003");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("대기");
        }

        @Test
        @DisplayName("NOT_HOLD_STATUS 에러 코드가 올바르다")
        void notHoldStatus() {
            SettlementErrorCode code = SettlementErrorCode.NOT_HOLD_STATUS;
            assertThat(code.getCode()).isEqualTo("STL-004");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("보류");
        }

        @Test
        @DisplayName("ALREADY_HOLD 에러 코드가 올바르다")
        void alreadyHold() {
            SettlementErrorCode code = SettlementErrorCode.ALREADY_HOLD;
            assertThat(code.getCode()).isEqualTo("STL-005");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("보류");
        }

        @Test
        @DisplayName("HOLD_REASON_REQUIRED 에러 코드가 올바르다")
        void holdReasonRequired() {
            SettlementErrorCode code = SettlementErrorCode.HOLD_REASON_REQUIRED;
            assertThat(code.getCode()).isEqualTo("STL-006");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("보류 사유");
        }

        @Test
        @DisplayName("HAS_HOLD_SETTLEMENT 에러 코드가 올바르다")
        void hasHoldSettlement() {
            SettlementErrorCode code = SettlementErrorCode.HAS_HOLD_SETTLEMENT;
            assertThat(code.getCode()).isEqualTo("STL-007");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("보류");
        }
    }

    @Nested
    @DisplayName("SettlementException 예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("SettlementException은 ErrorCode만으로 생성된다")
        void settlementExceptionWithErrorCode() {
            // when
            SettlementException exception =
                    new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND);

            // then
            assertThat(exception.code()).isEqualTo("STL-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("SettlementException은 커스텀 메시지를 지원한다")
        void settlementExceptionWithCustomMessage() {
            // when
            SettlementException exception =
                    new SettlementException(
                            SettlementErrorCode.INVALID_STATUS_TRANSITION,
                            "PENDING 상태에서 PENDING 상태로 변경할 수 없습니다");

            // then
            assertThat(exception.code()).isEqualTo("STL-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("PENDING 상태에서 PENDING 상태로 변경할 수 없습니다");
        }

        @Test
        @DisplayName("SettlementException은 원인 예외를 지원한다")
        void settlementExceptionWithCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            SettlementException exception =
                    new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("STL-001");
        }

        @Test
        @DisplayName("HOLD_REASON_REQUIRED 에러로 생성한 예외의 HTTP 상태는 400이다")
        void holdReasonRequiredExceptionHttpStatus() {
            // when
            SettlementException exception =
                    new SettlementException(SettlementErrorCode.HOLD_REASON_REQUIRED);

            // then
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.code()).isEqualTo("STL-006");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러로 생성한 예외의 상태 변경 메시지를 포함한다")
        void invalidTransitionExceptionWithContext() {
            // when
            SettlementException exception =
                    new SettlementException(
                            SettlementErrorCode.INVALID_STATUS_TRANSITION,
                            "COMPLETED 상태에서 HOLD 상태로 변경할 수 없습니다");

            // then
            assertThat(exception.getMessage()).contains("COMPLETED");
            assertThat(exception.getMessage()).contains("HOLD");
        }
    }
}
