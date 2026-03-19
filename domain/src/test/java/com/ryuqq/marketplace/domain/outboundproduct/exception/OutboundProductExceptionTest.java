package com.ryuqq.marketplace.domain.outboundproduct.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProduct 예외 테스트")
class OutboundProductExceptionTest {

    @Nested
    @DisplayName("OutboundProductErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("OUTBOUND_PRODUCT_NOT_FOUND 에러 코드가 올바르다")
        void outboundProductNotFound() {
            OutboundProductErrorCode code = OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("OBP-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("OUTBOUND_PRODUCT_ALREADY_REGISTERED 에러 코드가 올바르다")
        void outboundProductAlreadyRegistered() {
            OutboundProductErrorCode code =
                    OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED;

            assertThat(code.getCode()).isEqualTo("OBP-002");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("등록");
        }

        @Test
        @DisplayName("OUTBOUND_PRODUCT_INVALID_STATUS 에러 코드가 올바르다")
        void outboundProductInvalidStatus() {
            OutboundProductErrorCode code =
                    OutboundProductErrorCode.OUTBOUND_PRODUCT_INVALID_STATUS;

            assertThat(code.getCode()).isEqualTo("OBP-003");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("상태");
        }
    }

    @Nested
    @DisplayName("OutboundProductException 예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createWithErrorCode() {
            OutboundProductException exception =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("OBP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createWithCustomMessage() {
            OutboundProductException exception =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND,
                            "아웃바운드 상품 ID=999를 찾을 수 없습니다");

            assertThat(exception.getMessage()).isEqualTo("아웃바운드 상품 ID=999를 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("OBP-001");
        }

        @Test
        @DisplayName("원인 예외로 예외를 생성한다")
        void createWithCause() {
            RuntimeException cause = new RuntimeException("원인");
            OutboundProductException exception =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("OBP-001");
        }

        @Test
        @DisplayName("OutboundProductException은 DomainException이다")
        void isDomainException() {
            OutboundProductException exception =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND);

            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("ALREADY_REGISTERED 에러로 생성한 예외의 HTTP 상태는 409이다")
        void alreadyRegisteredExceptionHttpStatus() {
            OutboundProductException exception =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED);

            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
