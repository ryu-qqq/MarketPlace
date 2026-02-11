package com.ryuqq.marketplace.domain.brandpreset.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandPresetException 테스트")
class BrandPresetExceptionTest {

    @Nested
    @DisplayName("기본 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            BrandPresetException exception =
                    new BrandPresetException(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("브랜드 프리셋을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("BRDPRE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            BrandPresetException exception =
                    new BrandPresetException(
                            BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND, "ID 100 프리셋 없음");

            // then
            assertThat(exception.getMessage()).isEqualTo("ID 100 프리셋 없음");
            assertThat(exception.code()).isEqualTo("BRDPRE-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            BrandPresetException exception =
                    new BrandPresetException(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("BRDPRE-001");
        }
    }

    @Nested
    @DisplayName("구체적 예외 클래스 테스트")
    class ConcreteExceptionTest {

        @Test
        @DisplayName("BrandPresetNotFoundException 기본 생성")
        void createBrandPresetNotFoundException() {
            // when
            BrandPresetNotFoundException exception = new BrandPresetNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("BRDPRE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("브랜드 프리셋을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BrandPresetNotFoundException ID 포함 생성")
        void createBrandPresetNotFoundExceptionWithId() {
            // when
            BrandPresetNotFoundException exception = new BrandPresetNotFoundException(456L);

            // then
            assertThat(exception.code()).isEqualTo("BRDPRE-001");
            assertThat(exception.getMessage()).contains("456");
        }

        @Test
        @DisplayName("BrandPresetChannelMismatchException 생성")
        void createBrandPresetChannelMismatchException() {
            // when
            BrandPresetChannelMismatchException exception =
                    new BrandPresetChannelMismatchException(1L, 2L);

            // then
            assertThat(exception.code()).isEqualTo("BRDPRE-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("1").contains("2");
        }

        @Test
        @DisplayName("BrandPresetInternalBrandNotFoundException 생성")
        void createBrandPresetInternalBrandNotFoundException() {
            // when
            BrandPresetInternalBrandNotFoundException exception =
                    new BrandPresetInternalBrandNotFoundException(List.of(10L, 20L));

            // then
            assertThat(exception.code()).isEqualTo("BRDPRE-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("10").contains("20");
        }

        @Test
        @DisplayName("BrandPresetSalesChannelBrandNotFoundException 생성")
        void createBrandPresetSalesChannelBrandNotFoundException() {
            // when
            BrandPresetSalesChannelBrandNotFoundException exception =
                    new BrandPresetSalesChannelBrandNotFoundException(999L);

            // then
            assertThat(exception.code()).isEqualTo("BRDPRE-004");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("999");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("BrandPresetException은 DomainException을 상속한다")
        void brandPresetExceptionExtendsDomainException() {
            BrandPresetException exception =
                    new BrandPresetException(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND);

            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BrandPresetNotFoundException은 BrandPresetException을 상속한다")
        void brandPresetNotFoundExceptionExtendsBrandPresetException() {
            BrandPresetNotFoundException exception = new BrandPresetNotFoundException();

            assertThat(exception).isInstanceOf(BrandPresetException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BrandPresetChannelMismatchException은 BrandPresetException을 상속한다")
        void channelMismatchExceptionExtendsBrandPresetException() {
            BrandPresetChannelMismatchException exception =
                    new BrandPresetChannelMismatchException(1L, 2L);

            assertThat(exception).isInstanceOf(BrandPresetException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
