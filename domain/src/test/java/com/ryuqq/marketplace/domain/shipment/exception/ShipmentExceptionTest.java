package com.ryuqq.marketplace.domain.shipment.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Shipment 예외 테스트")
class ShipmentExceptionTest {

    @Nested
    @DisplayName("ShipmentErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("SHIPMENT_NOT_FOUND 에러 코드가 올바르다")
        void shipmentNotFound() {
            ShipmentErrorCode code = ShipmentErrorCode.SHIPMENT_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("SHP-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드가 올바르다")
        void invalidStatusTransition() {
            ShipmentErrorCode code = ShipmentErrorCode.INVALID_STATUS_TRANSITION;
            assertThat(code.getCode()).isEqualTo("SHP-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("유효하지 않은");
        }

        @Test
        @DisplayName("TRACKING_NUMBER_REQUIRED 에러 코드가 올바르다")
        void trackingNumberRequired() {
            ShipmentErrorCode code = ShipmentErrorCode.TRACKING_NUMBER_REQUIRED;
            assertThat(code.getCode()).isEqualTo("SHP-003");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("송장번호");
        }

        @Test
        @DisplayName("모든 에러 코드가 4개이다")
        void totalErrorCodeCount() {
            assertThat(ShipmentErrorCode.values()).hasSize(4);
        }
    }

    @Nested
    @DisplayName("ShipmentException 생성 테스트")
    class ShipmentExceptionCreationTest {

        @Test
        @DisplayName("에러 코드만으로 ShipmentException을 생성한다")
        void createWithErrorCode() {
            // when
            ShipmentException exception =
                    new ShipmentException(ShipmentErrorCode.INVALID_STATUS_TRANSITION);

            // then
            assertThat(exception.code()).isEqualTo("SHP-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("유효하지 않은");
        }

        @Test
        @DisplayName("에러 코드와 커스텀 메시지로 ShipmentException을 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            String customMessage = "배송 상태를 READY에서 SHIPPED(으)로 변경할 수 없습니다. 현재 상태: READY";

            // when
            ShipmentException exception =
                    new ShipmentException(
                            ShipmentErrorCode.INVALID_STATUS_TRANSITION, customMessage);

            // then
            assertThat(exception.getMessage()).isEqualTo(customMessage);
            assertThat(exception.code()).isEqualTo("SHP-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("에러 코드와 원인 예외로 ShipmentException을 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            ShipmentException exception =
                    new ShipmentException(ShipmentErrorCode.SHIPMENT_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("SHP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("ShipmentNotFoundException 테스트")
    class ShipmentNotFoundExceptionTest {

        @Test
        @DisplayName("기본 생성자로 ShipmentNotFoundException을 생성한다")
        void createWithDefaultConstructor() {
            // when
            ShipmentNotFoundException exception = new ShipmentNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("SHP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("배송 ID를 포함한 메시지로 ShipmentNotFoundException을 생성한다")
        void createWithShipmentId() {
            // given
            String shipmentId = "01944b2a-1234-7fff-8888-abcdef012345";

            // when
            ShipmentNotFoundException exception = new ShipmentNotFoundException(shipmentId);

            // then
            assertThat(exception.getMessage()).contains(shipmentId);
            assertThat(exception.code()).isEqualTo("SHP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ShipmentNotFoundException은 ShipmentException의 하위 타입이다")
        void shipmentNotFoundExceptionIsSubtypeOfShipmentException() {
            // when
            ShipmentNotFoundException exception = new ShipmentNotFoundException();

            // then
            assertThat(exception).isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("DomainException 공통 기능 테스트")
    class DomainExceptionCommonTest {

        @Test
        @DisplayName("ShipmentException은 RuntimeException의 하위 타입이다")
        void shipmentExceptionIsRuntimeException() {
            ShipmentException exception =
                    new ShipmentException(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("getErrorCode()로 에러 코드 객체를 반환한다")
        void getErrorCodeReturnsErrorCodeObject() {
            ShipmentException exception =
                    new ShipmentException(ShipmentErrorCode.TRACKING_NUMBER_REQUIRED);
            assertThat(exception.getErrorCode())
                    .isEqualTo(ShipmentErrorCode.TRACKING_NUMBER_REQUIRED);
        }
    }
}
