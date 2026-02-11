package com.ryuqq.marketplace.domain.brand.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandErrorCode 단위 테스트")
class BrandErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 정의 테스트")
    class ErrorCodeDefinitionTest {
        @Test
        @DisplayName("BRAND_NOT_FOUND는 404 상태 코드를 가진다")
        void brandNotFoundHas404Status() {
            // given
            BrandErrorCode errorCode = BrandErrorCode.BRAND_NOT_FOUND;

            // when & then
            assertThat(errorCode.getCode()).isEqualTo("BRD-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BRAND_CODE_DUPLICATE는 409 상태 코드를 가진다")
        void brandCodeDuplicateHas409Status() {
            // given
            BrandErrorCode errorCode = BrandErrorCode.BRAND_CODE_DUPLICATE;

            // when & then
            assertThat(errorCode.getCode()).isEqualTo("BRD-002");
            assertThat(errorCode.getHttpStatus()).isEqualTo(409);
            assertThat(errorCode.getMessage()).isEqualTo("이미 존재하는 브랜드 코드입니다");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeImplementationTest {
        @Test
        @DisplayName("모든 BrandErrorCode는 ErrorCode 인터페이스를 구현한다")
        void allErrorCodesImplementErrorCode() {
            // given
            BrandErrorCode[] errorCodes = BrandErrorCode.values();

            // when & then
            for (BrandErrorCode errorCode : errorCodes) {
                assertThat(errorCode.getCode()).isNotNull();
                assertThat(errorCode.getHttpStatus()).isGreaterThan(0);
                assertThat(errorCode.getMessage()).isNotNull();
            }
        }

        @Test
        @DisplayName("모든 BrandErrorCode는 고유한 에러 코드를 가진다")
        void allErrorCodesHaveUniqueCode() {
            // given
            BrandErrorCode[] errorCodes = BrandErrorCode.values();

            // when
            long uniqueCount =
                    java.util.Arrays.stream(errorCodes)
                            .map(BrandErrorCode::getCode)
                            .distinct()
                            .count();

            // then
            assertThat(uniqueCount).isEqualTo(errorCodes.length);
        }

        @Test
        @DisplayName("모든 에러 코드는 'BRD-' 접두사로 시작한다")
        void allErrorCodesStartWithBRDPrefix() {
            // given
            BrandErrorCode[] errorCodes = BrandErrorCode.values();

            // when & then
            for (BrandErrorCode errorCode : errorCodes) {
                assertThat(errorCode.getCode()).startsWith("BRD-");
            }
        }
    }

    @Nested
    @DisplayName("HTTP 상태 코드 테스트")
    class HttpStatusTest {
        @Test
        @DisplayName("모든 에러 코드는 유효한 HTTP 상태 코드를 가진다")
        void allErrorCodesHaveValidHttpStatus() {
            // given
            BrandErrorCode[] errorCodes = BrandErrorCode.values();

            // when & then
            for (BrandErrorCode errorCode : errorCodes) {
                int status = errorCode.getHttpStatus();
                assertThat(status).isBetween(400, 599);
            }
        }
    }

    @Nested
    @DisplayName("에러 메시지 테스트")
    class ErrorMessageTest {
        @Test
        @DisplayName("모든 에러 코드는 비어있지 않은 메시지를 가진다")
        void allErrorCodesHaveNonEmptyMessage() {
            // given
            BrandErrorCode[] errorCodes = BrandErrorCode.values();

            // when & then
            for (BrandErrorCode errorCode : errorCodes) {
                assertThat(errorCode.getMessage()).isNotBlank();
            }
        }
    }
}
