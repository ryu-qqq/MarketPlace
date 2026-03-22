package com.ryuqq.marketplace.domain.inboundqna.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundQnaException 단위 테스트")
class InboundQnaExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("에러 코드로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            InboundQnaException exception =
                    new InboundQnaException(InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND);
            assertThat(exception.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("에러 코드와 상세 메시지로 예외를 생성한다")
        void createWithErrorCodeAndDetail() {
            // when
            InboundQnaException exception =
                    new InboundQnaException(
                            InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND, "inboundQnaId=100");

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND);
        }

        @Test
        @DisplayName("에러 코드와 원인으로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            InboundQnaException exception =
                    new InboundQnaException(InboundQnaErrorCode.INVALID_STATUS_TRANSITION, cause);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(InboundQnaErrorCode.INVALID_STATUS_TRANSITION);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }
}
