package com.ryuqq.marketplace.domain.brandpreset.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandPresetErrorCode 테스트")
class BrandPresetErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            assertThat(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("브랜드 프리셋 에러 코드 테스트")
    class BrandPresetErrorCodesTest {

        @Test
        @DisplayName("BRAND_PRESET_NOT_FOUND 에러 코드를 검증한다")
        void brandPresetNotFound() {
            assertThat(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND.getCode())
                    .isEqualTo("BRDPRE-001");
            assertThat(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND.getMessage())
                    .isEqualTo("브랜드 프리셋을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BRAND_PRESET_CHANNEL_MISMATCH 에러 코드를 검증한다")
        void brandPresetChannelMismatch() {
            assertThat(BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH.getCode())
                    .isEqualTo("BRDPRE-002");
            assertThat(BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH.getHttpStatus())
                    .isEqualTo(400);
            assertThat(BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH.getMessage())
                    .isEqualTo("Shop과 브랜드의 판매채널이 일치하지 않습니다");
        }

        @Test
        @DisplayName("BRAND_PRESET_INTERNAL_BRAND_NOT_FOUND 에러 코드를 검증한다")
        void brandPresetInternalBrandNotFound() {
            assertThat(BrandPresetErrorCode.BRAND_PRESET_INTERNAL_BRAND_NOT_FOUND.getCode())
                    .isEqualTo("BRDPRE-003");
            assertThat(BrandPresetErrorCode.BRAND_PRESET_INTERNAL_BRAND_NOT_FOUND.getHttpStatus())
                    .isEqualTo(400);
            assertThat(BrandPresetErrorCode.BRAND_PRESET_INTERNAL_BRAND_NOT_FOUND.getMessage())
                    .isEqualTo("요청한 내부 브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BRAND_PRESET_SALES_CHANNEL_BRAND_NOT_FOUND 에러 코드를 검증한다")
        void brandPresetSalesChannelBrandNotFound() {
            assertThat(BrandPresetErrorCode.BRAND_PRESET_SALES_CHANNEL_BRAND_NOT_FOUND.getCode())
                    .isEqualTo("BRDPRE-004");
            assertThat(
                            BrandPresetErrorCode.BRAND_PRESET_SALES_CHANNEL_BRAND_NOT_FOUND
                                    .getHttpStatus())
                    .isEqualTo(404);
            assertThat(BrandPresetErrorCode.BRAND_PRESET_SALES_CHANNEL_BRAND_NOT_FOUND.getMessage())
                    .isEqualTo("판매채널 브랜드를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            assertThat(BrandPresetErrorCode.values())
                    .containsExactly(
                            BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND,
                            BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH,
                            BrandPresetErrorCode.BRAND_PRESET_INTERNAL_BRAND_NOT_FOUND,
                            BrandPresetErrorCode.BRAND_PRESET_SALES_CHANNEL_BRAND_NOT_FOUND);
        }
    }
}
