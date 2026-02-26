package com.ryuqq.marketplace.domain.shop.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopException 단위 테스트")
class ShopExceptionTest {

    @Nested
    @DisplayName("DomainException 상속 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("ShopException은 DomainException을 상속한다")
        void extendsDomainException() {
            // given
            ShopException exception = new ShopException(ShopErrorCode.SHOP_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("에러 코드로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            ShopException exception = new ShopException(ShopErrorCode.SHOP_NOT_FOUND);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("에러 코드와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            String customMessage = "외부몰 ID 123을 찾을 수 없습니다";

            // when
            ShopException exception =
                    new ShopException(ShopErrorCode.SHOP_NOT_FOUND, customMessage);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("에러 코드와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            Throwable cause = new RuntimeException("원인 예외");

            // when
            ShopException exception = new ShopException(ShopErrorCode.SHOP_NOT_FOUND, cause);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("ShopNotFoundException 테스트")
    class ShopNotFoundExceptionTest {

        @Test
        @DisplayName("ShopNotFoundException을 생성한다")
        void createShopNotFoundException() {
            // when
            ShopNotFoundException exception = new ShopNotFoundException();

            // then
            assertThat(exception).isInstanceOf(ShopException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND);
        }

        @Test
        @DisplayName("ShopId로 ShopNotFoundException을 생성한다")
        void createShopNotFoundExceptionWithShopId() {
            // given
            Long shopId = 123L;

            // when
            ShopNotFoundException exception = new ShopNotFoundException(shopId);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND);
            assertThat(exception.getMessage()).contains("123");
        }
    }

    @Nested
    @DisplayName("ShopAccountIdDuplicateException 테스트")
    class ShopAccountIdDuplicateExceptionTest {

        @Test
        @DisplayName("ShopAccountIdDuplicateException을 생성한다")
        void createShopAccountIdDuplicateException() {
            // when
            ShopAccountIdDuplicateException exception = new ShopAccountIdDuplicateException();

            // then
            assertThat(exception).isInstanceOf(ShopException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_ACCOUNT_DUPLICATE);
        }

        @Test
        @DisplayName("계정 ID로 ShopAccountIdDuplicateException을 생성한다")
        void createShopAccountIdDuplicateExceptionWithAccountId() {
            // given
            String accountId = "duplicate-account-123";

            // when
            ShopAccountIdDuplicateException exception =
                    new ShopAccountIdDuplicateException(accountId);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ShopErrorCode.SHOP_ACCOUNT_DUPLICATE);
            assertThat(exception.getMessage()).contains(accountId);
        }
    }

    @Nested
    @DisplayName("예외 전파 테스트")
    class ExceptionPropagationTest {

        @Test
        @DisplayName("ShopException은 RuntimeException이므로 unchecked 예외다")
        void shopExceptionIsUnchecked() {
            // given
            ShopException exception = new ShopException(ShopErrorCode.SHOP_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}
