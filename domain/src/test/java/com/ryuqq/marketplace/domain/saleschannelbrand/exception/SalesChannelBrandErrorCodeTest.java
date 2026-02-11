package com.ryuqq.marketplace.domain.saleschannelbrand.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandErrorCode 단위 테스트")
class SalesChannelBrandErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("에러 코드 상세 테스트")
    class ErrorCodeDetailTest {

        @Test
        @DisplayName("SALES_CHANNEL_BRAND_NOT_FOUND 에러 코드를 검증한다")
        void salesChannelBrandNotFound() {
            // then
            assertThat(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND.getCode())
                    .isEqualTo("SCBRD-001");
            assertThat(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND.getMessage())
                    .isEqualTo("외부 채널 브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SALES_CHANNEL_BRAND_CODE_DUPLICATE 에러 코드를 검증한다")
        void salesChannelBrandCodeDuplicate() {
            // then
            assertThat(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE.getCode())
                    .isEqualTo("SCBRD-002");
            assertThat(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE
                                    .getHttpStatus())
                    .isEqualTo(409);
            assertThat(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE
                                    .getMessage())
                    .isEqualTo("이미 존재하는 외부 브랜드 코드입니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SalesChannelBrandErrorCode.values())
                    .containsExactly(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND,
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE);
        }

        @Test
        @DisplayName("valueOf()로 에러 코드를 조회한다")
        void valueOfReturnsErrorCode() {
            // when
            SalesChannelBrandErrorCode errorCode =
                    SalesChannelBrandErrorCode.valueOf("SALES_CHANNEL_BRAND_NOT_FOUND");

            // then
            assertThat(errorCode)
                    .isEqualTo(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("HTTP 상태 코드 테스트")
    class HttpStatusTest {

        @Test
        @DisplayName("404 에러는 NOT FOUND 상태를 가진다")
        void notFoundHas404Status() {
            // then
            assertThat(SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
        }

        @Test
        @DisplayName("409 에러는 CONFLICT 상태를 가진다")
        void duplicateHas409Status() {
            // then
            assertThat(
                            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE
                                    .getHttpStatus())
                    .isEqualTo(409);
        }
    }
}
