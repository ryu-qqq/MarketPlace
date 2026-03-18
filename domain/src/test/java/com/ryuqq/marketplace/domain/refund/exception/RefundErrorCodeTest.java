package com.ryuqq.marketplace.domain.refund.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundErrorCode 테스트")
class RefundErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            assertThat(RefundErrorCode.REFUND_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("환불 에러 코드 개별 검증")
    class ErrorCodeValuesTest {

        @Test
        @DisplayName("REFUND_NOT_FOUND 에러 코드를 검증한다")
        void refundNotFound() {
            assertThat(RefundErrorCode.REFUND_NOT_FOUND.getCode()).isEqualTo("RFD-001");
            assertThat(RefundErrorCode.REFUND_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(RefundErrorCode.REFUND_NOT_FOUND.getMessage())
                    .isEqualTo("환불 클레임을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드를 검증한다")
        void invalidStatusTransition() {
            assertThat(RefundErrorCode.INVALID_STATUS_TRANSITION.getCode()).isEqualTo("RFD-002");
            assertThat(RefundErrorCode.INVALID_STATUS_TRANSITION.getHttpStatus()).isEqualTo(400);
            assertThat(RefundErrorCode.INVALID_STATUS_TRANSITION.getMessage())
                    .isEqualTo("유효하지 않은 환불 상태 변경입니다");
        }

        @Test
        @DisplayName("ALREADY_COMPLETED 에러 코드를 검증한다")
        void alreadyCompleted() {
            assertThat(RefundErrorCode.ALREADY_COMPLETED.getCode()).isEqualTo("RFD-003");
            assertThat(RefundErrorCode.ALREADY_COMPLETED.getHttpStatus()).isEqualTo(409);
            assertThat(RefundErrorCode.ALREADY_COMPLETED.getMessage()).isEqualTo("이미 완료된 환불입니다");
        }

        @Test
        @DisplayName("HOLD_REASON_REQUIRED 에러 코드를 검증한다")
        void holdReasonRequired() {
            assertThat(RefundErrorCode.HOLD_REASON_REQUIRED.getCode()).isEqualTo("RFD-004");
            assertThat(RefundErrorCode.HOLD_REASON_REQUIRED.getHttpStatus()).isEqualTo(400);
            assertThat(RefundErrorCode.HOLD_REASON_REQUIRED.getMessage()).isEqualTo("보류 사유는 필수입니다");
        }

        @Test
        @DisplayName("NOT_HOLD_STATUS 에러 코드를 검증한다")
        void notHoldStatus() {
            assertThat(RefundErrorCode.NOT_HOLD_STATUS.getCode()).isEqualTo("RFD-005");
            assertThat(RefundErrorCode.NOT_HOLD_STATUS.getHttpStatus()).isEqualTo(400);
            assertThat(RefundErrorCode.NOT_HOLD_STATUS.getMessage()).isEqualTo("보류 상태가 아닙니다");
        }

        @Test
        @DisplayName("ALREADY_HOLD 에러 코드를 검증한다")
        void alreadyHold() {
            assertThat(RefundErrorCode.ALREADY_HOLD.getCode()).isEqualTo("RFD-006");
            assertThat(RefundErrorCode.ALREADY_HOLD.getHttpStatus()).isEqualTo(409);
            assertThat(RefundErrorCode.ALREADY_HOLD.getMessage()).isEqualTo("이미 보류 상태입니다");
        }

        @Test
        @DisplayName("INVALID_REFUND_QTY 에러 코드를 검증한다")
        void invalidRefundQty() {
            assertThat(RefundErrorCode.INVALID_REFUND_QTY.getCode()).isEqualTo("RFD-007");
            assertThat(RefundErrorCode.INVALID_REFUND_QTY.getHttpStatus()).isEqualTo(400);
            assertThat(RefundErrorCode.INVALID_REFUND_QTY.getMessage())
                    .isEqualTo("환불 수량은 1 이상이어야 합니다");
        }

        @Test
        @DisplayName("REASON_UPDATE_NOT_ALLOWED 에러 코드를 검증한다")
        void reasonUpdateNotAllowed() {
            assertThat(RefundErrorCode.REASON_UPDATE_NOT_ALLOWED.getCode()).isEqualTo("RFD-008");
            assertThat(RefundErrorCode.REASON_UPDATE_NOT_ALLOWED.getHttpStatus()).isEqualTo(400);
            assertThat(RefundErrorCode.REASON_UPDATE_NOT_ALLOWED.getMessage())
                    .isEqualTo("현재 상태에서는 사유를 변경할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            assertThat(RefundErrorCode.values())
                    .containsExactly(
                            RefundErrorCode.REFUND_NOT_FOUND,
                            RefundErrorCode.INVALID_STATUS_TRANSITION,
                            RefundErrorCode.ALREADY_COMPLETED,
                            RefundErrorCode.HOLD_REASON_REQUIRED,
                            RefundErrorCode.NOT_HOLD_STATUS,
                            RefundErrorCode.ALREADY_HOLD,
                            RefundErrorCode.INVALID_REFUND_QTY,
                            RefundErrorCode.REASON_UPDATE_NOT_ALLOWED,
                            RefundErrorCode.REFUND_OWNERSHIP_MISMATCH);
        }
    }
}
