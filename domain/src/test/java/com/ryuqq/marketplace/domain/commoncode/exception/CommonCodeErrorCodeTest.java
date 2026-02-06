package com.ryuqq.marketplace.domain.commoncode.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeErrorCode 테스트")
class CommonCodeErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("COMMON_CODE_NOT_FOUND 테스트")
    class CommonCodeNotFoundTest {

        @Test
        @DisplayName("에러 코드는 'CMC-001'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND.getCode()).isEqualTo("CMC-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("메시지는 '공통 코드를 찾을 수 없습니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND.getMessage())
                    .isEqualTo("공통 코드를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("COMMON_CODE_DUPLICATE 테스트")
    class CommonCodeDuplicateTest {

        @Test
        @DisplayName("에러 코드는 'CMC-002'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_DUPLICATE.getCode()).isEqualTo("CMC-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_DUPLICATE.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("메시지는 '동일한 타입과 코드가 이미 존재합니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_DUPLICATE.getMessage())
                    .isEqualTo("동일한 타입과 코드가 이미 존재합니다");
        }
    }

    @Nested
    @DisplayName("COMMON_CODE_INACTIVE 테스트")
    class CommonCodeInactiveTest {

        @Test
        @DisplayName("에러 코드는 'CMC-003'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_INACTIVE.getCode()).isEqualTo("CMC-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_INACTIVE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '비활성화된 공통 코드입니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeErrorCode.COMMON_CODE_INACTIVE.getMessage())
                    .isEqualTo("비활성화된 공통 코드입니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(CommonCodeErrorCode.values())
                    .containsExactly(
                            CommonCodeErrorCode.COMMON_CODE_NOT_FOUND,
                            CommonCodeErrorCode.COMMON_CODE_DUPLICATE,
                            CommonCodeErrorCode.COMMON_CODE_INACTIVE,
                            CommonCodeErrorCode.INVALID_COMMON_CODE_TYPE,
                            CommonCodeErrorCode.INVALID_DISPLAY_ORDER);
        }
    }
}
