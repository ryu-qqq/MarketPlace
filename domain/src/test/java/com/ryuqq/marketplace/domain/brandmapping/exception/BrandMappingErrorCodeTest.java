package com.ryuqq.marketplace.domain.brandmapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandMappingErrorCode 테스트")
class BrandMappingErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("브랜드 매핑 에러 코드 테스트")
    class BrandMappingErrorCodesTest {

        @Test
        @DisplayName("BRAND_MAPPING_NOT_FOUND 에러 코드를 검증한다")
        void brandMappingNotFound() {
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND.getCode())
                    .isEqualTo("BRDMAP-001");
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND.getMessage())
                    .isEqualTo("브랜드 매핑을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BRAND_MAPPING_DUPLICATE 에러 코드를 검증한다")
        void brandMappingDuplicate() {
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_DUPLICATE.getCode())
                    .isEqualTo("BRDMAP-002");
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_DUPLICATE.getHttpStatus())
                    .isEqualTo(409);
            assertThat(BrandMappingErrorCode.BRAND_MAPPING_DUPLICATE.getMessage())
                    .isEqualTo("해당 외부 브랜드에 이미 매핑이 존재합니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            assertThat(BrandMappingErrorCode.values())
                    .containsExactly(
                            BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND,
                            BrandMappingErrorCode.BRAND_MAPPING_DUPLICATE);
        }
    }
}
