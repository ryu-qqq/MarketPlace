package com.ryuqq.marketplace.domain.productnotice.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductNoticeNotFoundException 테스트")
class ProductNoticeNotFoundExceptionTest {

    @Nested
    @DisplayName("예외 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("상품 고시정보 ID로 예외를 생성한다")
        void createExceptionWithProductNoticeId() {
            // given
            Long productNoticeId = 123L;

            // when
            ProductNoticeNotFoundException exception =
                    new ProductNoticeNotFoundException(productNoticeId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND);
            assertThat(exception.getMessage()).contains("상품 고시정보를 찾을 수 없습니다");
            assertThat(exception.getMessage()).contains("123");
        }
    }

    @Nested
    @DisplayName("예외 정보 테스트")
    class ExceptionInfoTest {

        @Test
        @DisplayName("예외는 상품 고시정보 ID를 컨텍스트에 포함한다")
        void exceptionContainsProductNoticeIdInContext() {
            // given
            Long productNoticeId = 456L;

            // when
            ProductNoticeNotFoundException exception =
                    new ProductNoticeNotFoundException(productNoticeId);

            // then
            assertThat(exception.args()).isNotNull();
            assertThat(exception.args()).containsEntry("productNoticeId", productNoticeId);
        }

        @Test
        @DisplayName("예외는 NOT_FOUND 에러 코드를 가진다")
        void exceptionHasNotFoundErrorCode() {
            // given
            Long productNoticeId = 789L;

            // when
            ProductNoticeNotFoundException exception =
                    new ProductNoticeNotFoundException(productNoticeId);

            // then
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(404);
            assertThat(exception.getErrorCode().getCode()).isEqualTo("PRDNTC-001");
        }
    }

    @Nested
    @DisplayName("예외 던지기 테스트")
    class ThrowingTest {

        @Test
        @DisplayName("예외를 던지면 메시지에 상품 고시정보 ID가 포함된다")
        void throwingExceptionContainsProductNoticeId() {
            // given
            Long productNoticeId = 999L;

            // when & then
            assertThatThrownBy(() -> { throw new ProductNoticeNotFoundException(productNoticeId); })
                    .isInstanceOf(ProductNoticeNotFoundException.class)
                    .hasMessageContaining("999")
                    .hasMessageContaining("상품 고시정보를 찾을 수 없습니다");
        }
    }
}
