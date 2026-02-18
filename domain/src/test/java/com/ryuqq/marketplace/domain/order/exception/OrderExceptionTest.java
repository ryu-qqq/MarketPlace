package com.ryuqq.marketplace.domain.order.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Order 예외 테스트")
class OrderExceptionTest {

    @Nested
    @DisplayName("OrderErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("ORDER_NOT_FOUND 에러 코드가 올바르다")
        void orderNotFound() {
            OrderErrorCode code = OrderErrorCode.ORDER_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("ORD-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드가 올바르다")
        void invalidStatusTransition() {
            OrderErrorCode code = OrderErrorCode.INVALID_STATUS_TRANSITION;
            assertThat(code.getCode()).isEqualTo("ORD-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("유효하지 않은");
        }

        @Test
        @DisplayName("INVALID_ORDER_DATA 에러 코드가 올바르다")
        void invalidOrderData() {
            OrderErrorCode code = OrderErrorCode.INVALID_ORDER_DATA;
            assertThat(code.getCode()).isEqualTo("ORD-003");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("ORDER_ALREADY_CANCELLED 에러 코드가 올바르다")
        void orderAlreadyCancelled() {
            OrderErrorCode code = OrderErrorCode.ORDER_ALREADY_CANCELLED;
            assertThat(code.getCode()).isEqualTo("ORD-004");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("ORDER_ALREADY_CONFIRMED 에러 코드가 올바르다")
        void orderAlreadyConfirmed() {
            OrderErrorCode code = OrderErrorCode.ORDER_ALREADY_CONFIRMED;
            assertThat(code.getCode()).isEqualTo("ORD-005");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("EMPTY_ORDER_ITEMS 에러 코드가 올바르다")
        void emptyOrderItems() {
            OrderErrorCode code = OrderErrorCode.EMPTY_ORDER_ITEMS;
            assertThat(code.getCode()).isEqualTo("ORD-006");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("주문 상품");
        }
    }

    @Nested
    @DisplayName("OrderException 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 OrderException을 생성한다")
        void createOrderExceptionWithErrorCode() {
            // when
            OrderException exception = new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);

            // then
            assertThat(exception.code()).isEqualTo("ORD-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 OrderException을 생성한다")
        void createOrderExceptionWithCustomMessage() {
            // given
            String customMessage = "ORDERED 상태에서 SHIPPED 상태로 변경할 수 없습니다";

            // when
            OrderException exception =
                    new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION, customMessage);

            // then
            assertThat(exception.getMessage()).isEqualTo(customMessage);
            assertThat(exception.code()).isEqualTo("ORD-002");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 OrderException을 생성한다")
        void createOrderExceptionWithCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            OrderException exception = new OrderException(OrderErrorCode.INVALID_ORDER_DATA, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("ORD-003");
        }
    }

    @Nested
    @DisplayName("OrderNotFoundException 클래스 테스트")
    class OrderNotFoundExceptionTest {

        @Test
        @DisplayName("기본 생성자로 OrderNotFoundException을 생성한다")
        void createOrderNotFoundException() {
            // when
            OrderNotFoundException exception = new OrderNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("ORD-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("orderId를 포함한 메시지로 OrderNotFoundException을 생성한다")
        void createOrderNotFoundExceptionWithOrderId() {
            // given
            String orderId = "01900000-0000-7000-8000-000000000001";

            // when
            OrderNotFoundException exception = new OrderNotFoundException(orderId);

            // then
            assertThat(exception.getMessage()).contains(orderId);
            assertThat(exception.code()).isEqualTo("ORD-001");
        }

        @Test
        @DisplayName("OrderNotFoundException은 OrderException의 하위 타입이다")
        void orderNotFoundExceptionIsSubtypeOfOrderException() {
            // when
            OrderNotFoundException exception = new OrderNotFoundException();

            // then
            assertThat(exception).isInstanceOf(OrderException.class);
        }
    }
}
