package com.ryuqq.marketplace.domain.shippingpolicy.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicyException 테스트")
class ShippingPolicyExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            ShippingPolicyException exception =
                    new ShippingPolicyException(ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("배송 정책을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("SHP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            ShippingPolicyException exception =
                    new ShippingPolicyException(
                            ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND, "ID 123 배송 정책 없음");

            // then
            assertThat(exception.getMessage()).isEqualTo("ID 123 배송 정책 없음");
            assertThat(exception.code()).isEqualTo("SHP-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            ShippingPolicyException exception =
                    new ShippingPolicyException(
                            ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("SHP-001");
        }
    }

    @Nested
    @DisplayName("정적 팩토리 메서드 테스트")
    class StaticFactoryMethodTest {

        @Test
        @DisplayName("policyNotFound()로 예외를 생성한다")
        void createWithPolicyNotFound() {
            // when
            ShippingPolicyException exception = ShippingPolicyException.policyNotFound();

            // then
            assertThat(exception.code()).isEqualTo("SHP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("배송 정책을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("policyInactive()로 예외를 생성한다")
        void createWithPolicyInactive() {
            // when
            ShippingPolicyException exception = ShippingPolicyException.policyInactive();

            // then
            assertThat(exception.code()).isEqualTo("SHP-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("비활성화된 배송 정책입니다");
        }

        @Test
        @DisplayName("invalidFreeThreshold()로 예외를 생성한다")
        void createWithInvalidFreeThreshold() {
            // when
            ShippingPolicyException exception = ShippingPolicyException.invalidFreeThreshold();

            // then
            assertThat(exception.code()).isEqualTo("SHP-005");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("무료배송 기준금액이 유효하지 않습니다");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void extendsDomainException() {
            // given
            ShippingPolicyException exception = ShippingPolicyException.policyNotFound();

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
