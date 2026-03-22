package com.ryuqq.marketplace.domain.qna.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaErrorCode 단위 테스트")
class QnaErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 속성 테스트")
    class ErrorCodePropertiesTest {

        @Test
        @DisplayName("QNA_NOT_FOUND는 404 상태 코드를 가진다")
        void qnaNotFoundHas404() {
            assertThat(QnaErrorCode.QNA_NOT_FOUND.getCode()).isEqualTo("QNA-001");
            assertThat(QnaErrorCode.QNA_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(QnaErrorCode.QNA_NOT_FOUND.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION는 400 상태 코드를 가진다")
        void invalidStatusTransitionHas400() {
            assertThat(QnaErrorCode.INVALID_STATUS_TRANSITION.getCode()).isEqualTo("QNA-002");
            assertThat(QnaErrorCode.INVALID_STATUS_TRANSITION.getHttpStatus()).isEqualTo(400);
            assertThat(QnaErrorCode.INVALID_STATUS_TRANSITION.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("ALREADY_ANSWERED는 409 상태 코드를 가진다")
        void alreadyAnsweredHas409() {
            assertThat(QnaErrorCode.ALREADY_ANSWERED.getCode()).isEqualTo("QNA-003");
            assertThat(QnaErrorCode.ALREADY_ANSWERED.getHttpStatus()).isEqualTo(409);
            assertThat(QnaErrorCode.ALREADY_ANSWERED.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("ALREADY_CLOSED는 409 상태 코드를 가진다")
        void alreadyClosedHas409() {
            assertThat(QnaErrorCode.ALREADY_CLOSED.getCode()).isEqualTo("QNA-004");
            assertThat(QnaErrorCode.ALREADY_CLOSED.getHttpStatus()).isEqualTo(409);
            assertThat(QnaErrorCode.ALREADY_CLOSED.getMessage()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("4개의 에러 코드가 정의되어 있다")
        void hasFourErrorCodes() {
            assertThat(QnaErrorCode.values()).hasSize(4);
        }

        @Test
        @DisplayName("모든 에러 코드는 비어있지 않은 코드와 메시지를 가진다")
        void allErrorCodesHaveNonBlankCodeAndMessage() {
            for (QnaErrorCode errorCode : QnaErrorCode.values()) {
                assertThat(errorCode.getCode()).isNotBlank();
                assertThat(errorCode.getMessage()).isNotBlank();
                assertThat(errorCode.getHttpStatus()).isPositive();
            }
        }
    }
}
