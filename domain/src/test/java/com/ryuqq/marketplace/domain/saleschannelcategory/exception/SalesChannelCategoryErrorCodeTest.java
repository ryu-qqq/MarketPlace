package com.ryuqq.marketplace.domain.saleschannelcategory.exception;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategoryErrorCode 테스트")
class SalesChannelCategoryErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("판매채널 카테고리 에러 코드 테스트")
    class SalesChannelCategoryErrorCodesTest {

        @Test
        @DisplayName("SALES_CHANNEL_CATEGORY_NOT_FOUND 에러 코드를 검증한다")
        void salesChannelCategoryNotFound() {
            // then
            assertThat(SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_NOT_FOUND.getCode())
                    .isEqualTo("SCCAT-001");
            assertThat(
                            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_NOT_FOUND
                                    .getHttpStatus())
                    .isEqualTo(404);
            assertThat(SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_NOT_FOUND.getMessage())
                    .isEqualTo("외부 채널 카테고리를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SALES_CHANNEL_CATEGORY_CODE_DUPLICATE 에러 코드를 검증한다")
        void salesChannelCategoryCodeDuplicate() {
            // then
            assertThat(
                            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_CODE_DUPLICATE
                                    .getCode())
                    .isEqualTo("SCCAT-002");
            assertThat(
                            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_CODE_DUPLICATE
                                    .getHttpStatus())
                    .isEqualTo(409);
            assertThat(
                            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_CODE_DUPLICATE
                                    .getMessage())
                    .isEqualTo("이미 존재하는 외부 카테고리 코드입니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SalesChannelCategoryErrorCode.values())
                    .containsExactly(
                            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_NOT_FOUND,
                            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_CODE_DUPLICATE);
        }
    }
}
