package com.ryuqq.marketplace.domain.inboundqna.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundQnaErrorCode 단위 테스트")
class InboundQnaErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 속성 테스트")
    class ErrorCodePropertiesTest {

        @Test
        @DisplayName("INBOUND_QNA_NOT_FOUND는 404 상태 코드를 가진다")
        void inboundQnaNotFoundHas404() {
            assertThat(InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND.getCode()).isEqualTo("IBQ-001");
            assertThat(InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION는 400 상태 코드를 가진다")
        void invalidStatusTransitionHas400() {
            assertThat(InboundQnaErrorCode.INVALID_STATUS_TRANSITION.getCode()).isEqualTo("IBQ-002");
            assertThat(InboundQnaErrorCode.INVALID_STATUS_TRANSITION.getHttpStatus()).isEqualTo(400);
            assertThat(InboundQnaErrorCode.INVALID_STATUS_TRANSITION.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("DUPLICATE_EXTERNAL_QNA는 409 상태 코드를 가진다")
        void duplicateExternalQnaHas409() {
            assertThat(InboundQnaErrorCode.DUPLICATE_EXTERNAL_QNA.getCode()).isEqualTo("IBQ-003");
            assertThat(InboundQnaErrorCode.DUPLICATE_EXTERNAL_QNA.getHttpStatus()).isEqualTo(409);
            assertThat(InboundQnaErrorCode.DUPLICATE_EXTERNAL_QNA.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("CONVERSION_FAILED는 500 상태 코드를 가진다")
        void conversionFailedHas500() {
            assertThat(InboundQnaErrorCode.CONVERSION_FAILED.getCode()).isEqualTo("IBQ-004");
            assertThat(InboundQnaErrorCode.CONVERSION_FAILED.getHttpStatus()).isEqualTo(500);
            assertThat(InboundQnaErrorCode.CONVERSION_FAILED.getMessage()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("4개의 에러 코드가 정의되어 있다")
        void hasFourErrorCodes() {
            assertThat(InboundQnaErrorCode.values()).hasSize(4);
        }

        @Test
        @DisplayName("모든 에러 코드는 비어있지 않은 코드와 메시지를 가진다")
        void allErrorCodesHaveNonBlankCodeAndMessage() {
            for (InboundQnaErrorCode errorCode : InboundQnaErrorCode.values()) {
                assertThat(errorCode.getCode()).isNotBlank();
                assertThat(errorCode.getMessage()).isNotBlank();
                assertThat(errorCode.getHttpStatus()).isPositive();
            }
        }
    }
}
