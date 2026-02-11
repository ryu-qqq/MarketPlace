package com.ryuqq.marketplace.domain.product.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductErrorCode Enum 테스트")
class ProductErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 속성 테스트")
    class ErrorCodeAttributesTest {

        @Test
        @DisplayName("PRODUCT_NOT_FOUND 에러 코드를 확인한다")
        void productNotFoundAttributes() {
            // when
            ProductErrorCode errorCode = ProductErrorCode.PRODUCT_NOT_FOUND;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PRD-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("상품을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("PRODUCT_INVALID_STATUS_TRANSITION 에러 코드를 확인한다")
        void productInvalidStatusTransitionAttributes() {
            // when
            ProductErrorCode errorCode = ProductErrorCode.PRODUCT_INVALID_STATUS_TRANSITION;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PRD-002");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("유효하지 않은 상태 전이입니다");
        }

        @Test
        @DisplayName("PRODUCT_INVALID_PRICE 에러 코드를 확인한다")
        void productInvalidPriceAttributes() {
            // when
            ProductErrorCode errorCode = ProductErrorCode.PRODUCT_INVALID_PRICE;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PRD-003");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("할인가는 판매가보다 클 수 없습니다");
        }
    }

    @Nested
    @DisplayName("에러 코드 목록 테스트")
    class ErrorCodeListTest {

        @Test
        @DisplayName("모든 에러 코드가 정의되어 있다")
        void allErrorCodesAreDefined() {
            // when
            ProductErrorCode[] errorCodes = ProductErrorCode.values();

            // then
            assertThat(errorCodes).hasSize(3);
            assertThat(errorCodes)
                    .containsExactlyInAnyOrder(
                            ProductErrorCode.PRODUCT_NOT_FOUND,
                            ProductErrorCode.PRODUCT_INVALID_STATUS_TRANSITION,
                            ProductErrorCode.PRODUCT_INVALID_PRICE);
        }

        @Test
        @DisplayName("에러 코드는 고유한 코드 값을 가진다")
        void errorCodesHaveUniqueCodeValues() {
            // when
            ProductErrorCode[] errorCodes = ProductErrorCode.values();

            // then
            assertThat(errorCodes)
                    .extracting(ProductErrorCode::getCode)
                    .containsExactlyInAnyOrder("PRD-001", "PRD-002", "PRD-003");
        }
    }
}
