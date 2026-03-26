package com.ryuqq.marketplace.domain.inboundproduct.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundProduct 예외 테스트")
class InboundProductExceptionTest {

    @Nested
    @DisplayName("InboundProductErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("INBOUND_PRODUCT_NOT_FOUND 에러 코드가 올바르다")
        void inboundProductNotFound() {
            InboundProductErrorCode code = InboundProductErrorCode.INBOUND_PRODUCT_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("IBP-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_INVALID_STATUS 에러 코드가 올바르다")
        void inboundProductInvalidStatus() {
            InboundProductErrorCode code = InboundProductErrorCode.INBOUND_PRODUCT_INVALID_STATUS;

            assertThat(code.getCode()).isEqualTo("IBP-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_CONVERSION_FAILED 에러 코드가 올바르다")
        void inboundProductConversionFailed() {
            InboundProductErrorCode code =
                    InboundProductErrorCode.INBOUND_PRODUCT_CONVERSION_FAILED;

            assertThat(code.getCode()).isEqualTo("IBP-004");
            assertThat(code.getHttpStatus()).isEqualTo(500);
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_PAYLOAD_INVALID 에러 코드가 올바르다")
        void inboundProductPayloadInvalid() {
            InboundProductErrorCode code = InboundProductErrorCode.INBOUND_PRODUCT_PAYLOAD_INVALID;

            assertThat(code.getCode()).isEqualTo("IBP-008");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_NOT_YET_CONVERTED 에러 코드가 올바르다")
        void inboundProductNotYetConverted() {
            InboundProductErrorCode code =
                    InboundProductErrorCode.INBOUND_PRODUCT_NOT_YET_CONVERTED;

            assertThat(code.getCode()).isEqualTo("IBP-009");
            assertThat(code.getHttpStatus()).isEqualTo(422);
        }
    }

    @Nested
    @DisplayName("InboundProductException 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createExceptionWithErrorCode() {
            InboundProductException exception =
                    new InboundProductException(InboundProductErrorCode.INBOUND_PRODUCT_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("IBP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createExceptionWithCustomMessage() {
            InboundProductException exception =
                    new InboundProductException(
                            InboundProductErrorCode.INBOUND_PRODUCT_NOT_FOUND,
                            "inboundProductId=999 상품을 찾을 수 없습니다");

            assertThat(exception.getMessage()).isEqualTo("inboundProductId=999 상품을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("원인 예외를 포함하여 예외를 생성한다")
        void createExceptionWithCause() {
            RuntimeException cause = new RuntimeException("DB 연결 실패");

            InboundProductException exception =
                    new InboundProductException(
                            InboundProductErrorCode.INBOUND_PRODUCT_CONVERSION_FAILED, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("IBP-004");
        }
    }

    @Nested
    @DisplayName("특수 예외 클래스 테스트")
    class SpecialExceptionTest {

        @Test
        @DisplayName("InboundProductNotFoundException은 NOT_FOUND 에러코드를 사용한다")
        void notFoundExceptionUsesCorrectErrorCode() {
            InboundProductNotFoundException exception =
                    new InboundProductNotFoundException(10L, "EXT-PROD-001");

            assertThat(exception.code()).isEqualTo("IBP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("10");
            assertThat(exception.getMessage()).contains("EXT-PROD-001");
        }

        @Test
        @DisplayName("InboundProductMappingNotReadyException은 MAPPING_FAILED 에러코드를 사용한다")
        void mappingNotReadyExceptionUsesCorrectErrorCode() {
            InboundProductMappingNotReadyException exception =
                    new InboundProductMappingNotReadyException(42L);

            assertThat(exception.code()).isEqualTo("IBP-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("42");
        }

        @Test
        @DisplayName("InboundProductConversionFailedException은 CONVERSION_FAILED 에러코드를 사용한다")
        void conversionFailedExceptionUsesCorrectErrorCode() {
            InboundProductConversionFailedException exception =
                    new InboundProductConversionFailedException(99L);

            assertThat(exception.code()).isEqualTo("IBP-004");
            assertThat(exception.httpStatus()).isEqualTo(500);
            assertThat(exception.getMessage()).contains("99");
        }
    }
}
