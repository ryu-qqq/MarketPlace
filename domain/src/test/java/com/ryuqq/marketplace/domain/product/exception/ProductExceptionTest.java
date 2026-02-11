package com.ryuqq.marketplace.domain.product.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Product 도메인 예외 테스트")
class ProductExceptionTest {

    @Nested
    @DisplayName("ProductNotFoundException 테스트")
    class ProductNotFoundExceptionTest {

        @Test
        @DisplayName("상품 ID로 ProductNotFoundException을 생성한다")
        void createProductNotFoundException() {
            // given
            Long productId = 123L;

            // when
            ProductNotFoundException exception = new ProductNotFoundException(productId);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
            assertThat(exception.getMessage()).contains("상품을 찾을 수 없습니다");
            assertThat(exception.getMessage()).contains("123");
            assertThat(exception.args()).containsEntry("productId", productId);
        }

        @Test
        @DisplayName("ProductNotFoundException을 던진다")
        void throwProductNotFoundException() {
            // given
            Long productId = 999L;

            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new ProductNotFoundException(productId);
                            })
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다")
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("ProductInvalidStatusTransitionException 테스트")
    class ProductInvalidStatusTransitionExceptionTest {

        @Test
        @DisplayName("상태 전이 예외를 생성한다")
        void createProductInvalidStatusTransitionException() {
            // given
            ProductStatus currentStatus = ProductStatus.ACTIVE;
            ProductStatus targetStatus = ProductStatus.ACTIVE;

            // when
            ProductInvalidStatusTransitionException exception =
                    new ProductInvalidStatusTransitionException(currentStatus, targetStatus);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_INVALID_STATUS_TRANSITION);
            assertThat(exception.getMessage()).contains("상품 상태를");
            assertThat(exception.getMessage()).contains("ACTIVE");
            assertThat(exception.args()).containsEntry("currentStatus", "ACTIVE");
            assertThat(exception.args()).containsEntry("targetStatus", "ACTIVE");
        }

        @Test
        @DisplayName("잘못된 상태 전이 시 예외를 던진다")
        void throwProductInvalidStatusTransitionException() {
            // given
            ProductStatus currentStatus = ProductStatus.DELETED;
            ProductStatus targetStatus = ProductStatus.ACTIVE;

            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new ProductInvalidStatusTransitionException(
                                        currentStatus, targetStatus);
                            })
                    .isInstanceOf(ProductInvalidStatusTransitionException.class)
                    .hasMessageContaining("상품 상태를")
                    .hasMessageContaining("DELETED")
                    .hasMessageContaining("ACTIVE");
        }
    }

    @Nested
    @DisplayName("ProductInvalidPriceException 테스트")
    class ProductInvalidPriceExceptionTest {

        @Test
        @DisplayName("가격 체계 예외를 생성한다")
        void createProductInvalidPriceException() {
            // given
            int regularPrice = 100000;
            int currentPrice = 120000;
            int salePrice = 90000;

            // when
            ProductInvalidPriceException exception =
                    new ProductInvalidPriceException(regularPrice, currentPrice, salePrice);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_INVALID_PRICE);
            assertThat(exception.getMessage()).contains("가격 체계가 유효하지 않습니다");
            assertThat(exception.getMessage()).contains("regularPrice=100000");
            assertThat(exception.getMessage()).contains("currentPrice=120000");
            assertThat(exception.getMessage()).contains("salePrice=90000");
            assertThat(exception.args()).containsEntry("regularPrice", regularPrice);
            assertThat(exception.args()).containsEntry("currentPrice", currentPrice);
            assertThat(exception.args()).containsEntry("salePrice", salePrice);
        }

        @Test
        @DisplayName("잘못된 가격 체계 시 예외를 던진다")
        void throwProductInvalidPriceException() {
            // given
            int regularPrice = 100000;
            int currentPrice = 150000; // regularPrice보다 큼
            int salePrice = 80000;

            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new ProductInvalidPriceException(
                                        regularPrice, currentPrice, salePrice);
                            })
                    .isInstanceOf(ProductInvalidPriceException.class)
                    .hasMessageContaining("가격 체계가 유효하지 않습니다")
                    .hasMessageContaining("regularPrice=100000")
                    .hasMessageContaining("currentPrice=150000");
        }
    }

    @Nested
    @DisplayName("예외 계층 구조 테스트")
    class ExceptionHierarchyTest {

        @Test
        @DisplayName("ProductNotFoundException은 DomainException을 상속한다")
        void productNotFoundExceptionExtendsDomainException() {
            // when
            ProductNotFoundException exception = new ProductNotFoundException(1L);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("ProductInvalidStatusTransitionException은 DomainException을 상속한다")
        void productInvalidStatusTransitionExceptionExtendsDomainException() {
            // when
            ProductInvalidStatusTransitionException exception =
                    new ProductInvalidStatusTransitionException(
                            ProductStatus.ACTIVE, ProductStatus.INACTIVE);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("ProductInvalidPriceException은 DomainException을 상속한다")
        void productInvalidPriceExceptionExtendsDomainException() {
            // when
            ProductInvalidPriceException exception =
                    new ProductInvalidPriceException(100000, 120000, 80000);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}
