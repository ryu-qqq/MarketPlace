package com.ryuqq.marketplace.domain.cancel.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Cancel 예외 테스트")
class CancelExceptionTest {

    @Nested
    @DisplayName("CancelErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("CANCEL_NOT_FOUND 에러 코드가 올바르다")
        void cancelNotFound() {
            CancelErrorCode code = CancelErrorCode.CANCEL_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("CAN-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드가 올바르다")
        void invalidStatusTransition() {
            CancelErrorCode code = CancelErrorCode.INVALID_STATUS_TRANSITION;
            assertThat(code.getCode()).isEqualTo("CAN-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("유효하지 않은");
        }

        @Test
        @DisplayName("ALREADY_CANCELLED 에러 코드가 올바르다")
        void alreadyCancelled() {
            CancelErrorCode code = CancelErrorCode.ALREADY_CANCELLED;
            assertThat(code.getCode()).isEqualTo("CAN-003");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("이미 취소된");
        }

        @Test
        @DisplayName("INVALID_CANCEL_REASON 에러 코드가 올바르다")
        void invalidCancelReason() {
            CancelErrorCode code = CancelErrorCode.INVALID_CANCEL_REASON;
            assertThat(code.getCode()).isEqualTo("CAN-004");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("취소 사유");
        }

        @Test
        @DisplayName("INVALID_CANCEL_QTY 에러 코드가 올바르다")
        void invalidCancelQty() {
            CancelErrorCode code = CancelErrorCode.INVALID_CANCEL_QTY;
            assertThat(code.getCode()).isEqualTo("CAN-005");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("취소 수량");
        }

        @Test
        @DisplayName("ORDER_NOT_CANCELLABLE 에러 코드가 올바르다")
        void orderNotCancellable() {
            CancelErrorCode code = CancelErrorCode.ORDER_NOT_CANCELLABLE;
            assertThat(code.getCode()).isEqualTo("CAN-006");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("취소할 수 없는");
        }

        @Test
        @DisplayName("EMPTY_CANCEL_ITEMS 에러 코드가 올바르다")
        void emptyCancelItems() {
            CancelErrorCode code = CancelErrorCode.EMPTY_CANCEL_ITEMS;
            assertThat(code.getCode()).isEqualTo("CAN-007");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("최소 1개");
        }
    }

    @Nested
    @DisplayName("CancelException 생성자 테스트")
    class ExceptionCreationTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createWithErrorCodeOnly() {
            // when
            CancelException exception = new CancelException(CancelErrorCode.CANCEL_NOT_FOUND);

            // then
            assertThat(exception.code()).isEqualTo("CAN-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("커스텀 메시지를 포함한 예외를 생성한다")
        void createWithCustomMessage() {
            // when
            CancelException exception =
                    new CancelException(
                            CancelErrorCode.CANCEL_NOT_FOUND, "취소 ID: cancel-001을 찾을 수 없습니다");

            // then
            assertThat(exception.code()).isEqualTo("CAN-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("취소 ID: cancel-001을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("원인 예외를 포함한 예외를 생성한다")
        void createWithCause() {
            // given
            RuntimeException cause = new RuntimeException("DB 접속 실패");

            // when
            CancelException exception =
                    new CancelException(CancelErrorCode.CANCEL_NOT_FOUND, cause);

            // then
            assertThat(exception.code()).isEqualTo("CAN-001");
            assertThat(exception.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 예외는 400 상태코드를 가진다")
        void invalidStatusTransitionExceptionHas400Status() {
            // when
            CancelException exception =
                    new CancelException(
                            CancelErrorCode.INVALID_STATUS_TRANSITION,
                            "REQUESTED 상태에서 COMPLETED 상태로 변경할 수 없습니다");

            // then
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("CancelException은 DomainException을 상속한다")
        void cancelExceptionExtendsDomainException() {
            // when
            CancelException exception = new CancelException(CancelErrorCode.CANCEL_NOT_FOUND);

            // then
            assertThat(exception)
                    .isInstanceOf(
                            com.ryuqq.marketplace.domain.common.exception.DomainException.class);
        }
    }
}
