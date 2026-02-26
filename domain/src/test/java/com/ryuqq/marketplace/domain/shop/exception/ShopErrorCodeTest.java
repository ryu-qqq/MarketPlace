package com.ryuqq.marketplace.domain.shop.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopErrorCode 단위 테스트")
class ShopErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(ShopErrorCode.SHOP_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("Shop 관련 에러 코드 테스트")
    class ShopErrorCodesTest {

        @Test
        @DisplayName("SHOP_NOT_FOUND 에러 코드를 검증한다")
        void shopNotFound() {
            // then
            assertThat(ShopErrorCode.SHOP_NOT_FOUND.getCode()).isEqualTo("SHP-001");
            assertThat(ShopErrorCode.SHOP_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(ShopErrorCode.SHOP_NOT_FOUND.getMessage()).isEqualTo("외부몰을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SHOP_ACCOUNT_DUPLICATE 에러 코드를 검증한다")
        void shopAccountDuplicate() {
            // then
            assertThat(ShopErrorCode.SHOP_ACCOUNT_DUPLICATE.getCode()).isEqualTo("SHP-003");
            assertThat(ShopErrorCode.SHOP_ACCOUNT_DUPLICATE.getHttpStatus()).isEqualTo(409);
            assertThat(ShopErrorCode.SHOP_ACCOUNT_DUPLICATE.getMessage())
                    .isEqualTo("해당 판매채널에 이미 존재하는 계정 ID입니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ShopErrorCode.values())
                    .containsExactly(
                            ShopErrorCode.SHOP_NOT_FOUND, ShopErrorCode.SHOP_ACCOUNT_DUPLICATE);
        }

        @Test
        @DisplayName("valueOf()로 에러 코드를 가져온다")
        void valueOfErrorCode() {
            // when
            ShopErrorCode errorCode = ShopErrorCode.valueOf("SHOP_NOT_FOUND");

            // then
            assertThat(errorCode).isEqualTo(ShopErrorCode.SHOP_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("HTTP 상태 코드 테스트")
    class HttpStatusTest {

        @Test
        @DisplayName("NOT_FOUND 에러는 404 상태 코드를 반환한다")
        void notFoundErrorReturns404() {
            // then
            assertThat(ShopErrorCode.SHOP_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("중복 에러는 409 상태 코드를 반환한다")
        void duplicateErrorsReturn409() {
            // then
            assertThat(ShopErrorCode.SHOP_ACCOUNT_DUPLICATE.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("에러 메시지 테스트")
    class ErrorMessageTest {

        @Test
        @DisplayName("모든 에러 코드는 한글 메시지를 가진다")
        void allErrorCodesHaveKoreanMessage() {
            // then
            for (ShopErrorCode errorCode : ShopErrorCode.values()) {
                assertThat(errorCode.getMessage()).isNotBlank().matches(".*[가-힣]+.*"); // 한글 포함 확인
            }
        }

        @Test
        @DisplayName("모든 에러 코드는 고유한 코드를 가진다")
        void allErrorCodesHaveUniqueCode() {
            // then
            ShopErrorCode[] errorCodes = ShopErrorCode.values();
            for (int i = 0; i < errorCodes.length; i++) {
                for (int j = i + 1; j < errorCodes.length; j++) {
                    assertThat(errorCodes[i].getCode()).isNotEqualTo(errorCodes[j].getCode());
                }
            }
        }
    }
}
