package com.ryuqq.marketplace.domain.saleschannelbrand.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandException 단위 테스트")
class SalesChannelBrandExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("에러 코드로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("외부 채널 브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("에러 코드와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            String customMessage = "ID 123의 외부 채널 브랜드를 찾을 수 없습니다";

            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND,
                            customMessage);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("에러 코드와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            Throwable cause = new RuntimeException("원인 예외");

            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND, cause);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("예외 타입 테스트")
    class ExceptionTypeTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void extendsDomainException() {
            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("RuntimeException을 상속한다")
        void extendsRuntimeException() {
            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("에러 코드별 예외 생성 테스트")
    class ErrorCodeSpecificTest {

        @Test
        @DisplayName("SALES_CHANNEL_BRAND_NOT_FOUND 예외를 생성한다")
        void createNotFoundExceptionWithErrorCode() {
            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);

            // then
            assertThat(exception.getErrorCode().getCode()).isEqualTo("SCBRD-001");
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("SALES_CHANNEL_BRAND_CODE_DUPLICATE 예외를 생성한다")
        void createCodeDuplicateExceptionWithErrorCode() {
            // when
            SalesChannelBrandException exception =
                    new SalesChannelBrandException(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE);

            // then
            assertThat(exception.getErrorCode().getCode()).isEqualTo("SCBRD-002");
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(409);
        }
    }
}
