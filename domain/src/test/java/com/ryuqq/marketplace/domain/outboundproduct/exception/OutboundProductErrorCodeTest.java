package com.ryuqq.marketplace.domain.outboundproduct.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductErrorCode enum 단위 테스트")
class OutboundProductErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 값 검증")
    class ErrorCodeValueTest {

        @Test
        @DisplayName("OUTBOUND_PRODUCT_NOT_FOUND의 코드는 OBP-001이고 HTTP 상태는 404이다")
        void notFoundErrorCode() {
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND.getCode())
                    .isEqualTo("OBP-001");
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND.getMessage())
                    .isNotBlank();
        }

        @Test
        @DisplayName("OUTBOUND_PRODUCT_ALREADY_REGISTERED의 코드는 OBP-002이고 HTTP 상태는 409이다")
        void alreadyRegisteredErrorCode() {
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED.getCode())
                    .isEqualTo("OBP-002");
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED.getHttpStatus())
                    .isEqualTo(409);
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED.getMessage())
                    .isNotBlank();
        }

        @Test
        @DisplayName("OUTBOUND_PRODUCT_INVALID_STATUS의 코드는 OBP-003이고 HTTP 상태는 400이다")
        void invalidStatusErrorCode() {
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_INVALID_STATUS.getCode())
                    .isEqualTo("OBP-003");
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_INVALID_STATUS.getHttpStatus())
                    .isEqualTo(400);
            assertThat(OutboundProductErrorCode.OUTBOUND_PRODUCT_INVALID_STATUS.getMessage())
                    .isNotBlank();
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 검증")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("모든 에러 코드는 getCode(), getHttpStatus(), getMessage()가 유효하다")
        void allErrorCodesHaveValidFields() {
            for (OutboundProductErrorCode errorCode : OutboundProductErrorCode.values()) {
                assertThat(errorCode.getCode()).isNotNull().isNotBlank();
                assertThat(errorCode.getHttpStatus()).isPositive();
                assertThat(errorCode.getMessage()).isNotNull().isNotBlank();
            }
        }
    }

    @Nested
    @DisplayName("enum 값 개수 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("OutboundProductErrorCode는 3가지 값을 가진다")
        void has3Values() {
            assertThat(OutboundProductErrorCode.values()).hasSize(3);
        }
    }
}
