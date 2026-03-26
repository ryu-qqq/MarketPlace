package com.ryuqq.marketplace.domain.inboundsource.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundSource 예외 테스트")
class InboundSourceExceptionTest {

    @Nested
    @DisplayName("InboundSourceErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("INBOUND_SOURCE_NOT_FOUND 에러 코드가 올바르다")
        void notFoundErrorCode() {
            InboundSourceErrorCode code = InboundSourceErrorCode.INBOUND_SOURCE_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("EXS-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INBOUND_SOURCE_CODE_DUPLICATE 에러 코드가 올바르다")
        void duplicateErrorCode() {
            InboundSourceErrorCode code = InboundSourceErrorCode.INBOUND_SOURCE_CODE_DUPLICATE;

            assertThat(code.getCode()).isEqualTo("EXS-002");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("InboundSourceException 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createExceptionWithErrorCode() {
            InboundSourceException exception =
                    new InboundSourceException(InboundSourceErrorCode.INBOUND_SOURCE_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("EXS-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createExceptionWithCustomMessage() {
            InboundSourceException exception =
                    new InboundSourceException(
                            InboundSourceErrorCode.INBOUND_SOURCE_CODE_DUPLICATE,
                            "소스 코드 SETOF는 이미 존재합니다");

            assertThat(exception.getMessage()).contains("SETOF");
        }

        @Test
        @DisplayName("원인 예외를 포함하여 예외를 생성한다")
        void createExceptionWithCause() {
            RuntimeException cause = new RuntimeException("DB 오류");

            InboundSourceException exception =
                    new InboundSourceException(
                            InboundSourceErrorCode.INBOUND_SOURCE_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
