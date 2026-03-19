package com.ryuqq.marketplace.domain.inboundorder.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundOrder 예외 테스트")
class InboundOrderExceptionTest {

    @Nested
    @DisplayName("InboundOrderErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("INBOUND_ORDER_NOT_FOUND 에러 코드가 올바르다")
        void inboundOrderNotFound() {
            InboundOrderErrorCode code = InboundOrderErrorCode.INBOUND_ORDER_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("IBO-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드가 올바르다")
        void invalidStatusTransition() {
            InboundOrderErrorCode code = InboundOrderErrorCode.INVALID_STATUS_TRANSITION;

            assertThat(code.getCode()).isEqualTo("IBO-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("DUPLICATE_EXTERNAL_ORDER 에러 코드가 올바르다")
        void duplicateExternalOrder() {
            InboundOrderErrorCode code = InboundOrderErrorCode.DUPLICATE_EXTERNAL_ORDER;

            assertThat(code.getCode()).isEqualTo("IBO-003");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("CONVERSION_FAILED 에러 코드가 올바르다")
        void conversionFailed() {
            InboundOrderErrorCode code = InboundOrderErrorCode.CONVERSION_FAILED;

            assertThat(code.getCode()).isEqualTo("IBO-005");
            assertThat(code.getHttpStatus()).isEqualTo(500);
        }
    }

    @Nested
    @DisplayName("InboundOrderException 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createExceptionWithErrorCode() {
            InboundOrderException exception =
                    new InboundOrderException(InboundOrderErrorCode.INBOUND_ORDER_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("IBO-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createExceptionWithCustomMessage() {
            InboundOrderException exception =
                    new InboundOrderException(
                            InboundOrderErrorCode.DUPLICATE_EXTERNAL_ORDER,
                            "NAVER-ORD-001은 이미 처리되었습니다");

            assertThat(exception.getMessage()).isEqualTo("NAVER-ORD-001은 이미 처리되었습니다");
        }

        @Test
        @DisplayName("원인 예외를 포함하여 예외를 생성한다")
        void createExceptionWithCause() {
            RuntimeException cause = new RuntimeException("외부 API 오류");

            InboundOrderException exception =
                    new InboundOrderException(InboundOrderErrorCode.CONVERSION_FAILED, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.httpStatus()).isEqualTo(500);
        }
    }
}
