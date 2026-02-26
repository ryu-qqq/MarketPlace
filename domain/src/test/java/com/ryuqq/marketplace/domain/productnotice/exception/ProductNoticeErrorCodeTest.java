package com.ryuqq.marketplace.domain.productnotice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductNoticeErrorCode 테스트")
class ProductNoticeErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 정의 테스트")
    class ErrorCodeDefinitionTest {

        @Test
        @DisplayName("PRODUCT_NOTICE_NOT_FOUND 에러 코드가 올바르게 정의되어 있다")
        void productNoticeNotFoundIsDefinedCorrectly() {
            // when
            ProductNoticeErrorCode errorCode = ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PRDNTC-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("상품 고시정보를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("getCode()는 에러 코드를 반환한다")
        void getCodeReturnsCode() {
            // given
            ProductNoticeErrorCode errorCode = ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND;

            // when & then
            assertThat(errorCode.getCode()).isNotNull();
            assertThat(errorCode.getCode()).isNotBlank();
        }

        @Test
        @DisplayName("getHttpStatus()는 HTTP 상태 코드를 반환한다")
        void getHttpStatusReturnsStatusCode() {
            // given
            ProductNoticeErrorCode errorCode = ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND;

            // when & then
            assertThat(errorCode.getHttpStatus()).isPositive();
            assertThat(errorCode.getHttpStatus()).isGreaterThanOrEqualTo(400);
            assertThat(errorCode.getHttpStatus()).isLessThan(600);
        }

        @Test
        @DisplayName("getMessage()는 에러 메시지를 반환한다")
        void getMessageReturnsMessage() {
            // given
            ProductNoticeErrorCode errorCode = ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND;

            // when & then
            assertThat(errorCode.getMessage()).isNotNull();
            assertThat(errorCode.getMessage()).isNotBlank();
        }
    }
}
