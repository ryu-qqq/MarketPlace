package com.ryuqq.marketplace.domain.commoncodetype.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeErrorCode 테스트")
class CommonCodeTypeErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(CommonCodeTypeErrorCode.NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("NOT_FOUND 테스트")
    class NotFoundTest {

        @Test
        @DisplayName("에러 코드는 'CCT-001'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeTypeErrorCode.NOT_FOUND.getCode()).isEqualTo("CCT-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeTypeErrorCode.NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("메시지는 '공통 코드 타입을 찾을 수 없습니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeTypeErrorCode.NOT_FOUND.getMessage())
                    .isEqualTo("공통 코드 타입을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_CODE 테스트")
    class DuplicateCodeTest {

        @Test
        @DisplayName("에러 코드는 'CCT-002'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeTypeErrorCode.DUPLICATE_CODE.getCode()).isEqualTo("CCT-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeTypeErrorCode.DUPLICATE_CODE.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("메시지는 '동일한 코드가 이미 존재합니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeTypeErrorCode.DUPLICATE_CODE.getMessage())
                    .isEqualTo("동일한 코드가 이미 존재합니다");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_DISPLAY_ORDER 테스트")
    class DuplicateDisplayOrderTest {

        @Test
        @DisplayName("에러 코드는 'CCT-003'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeTypeErrorCode.DUPLICATE_DISPLAY_ORDER.getCode())
                    .isEqualTo("CCT-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeTypeErrorCode.DUPLICATE_DISPLAY_ORDER.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '동일한 표시 순서가 이미 존재합니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeTypeErrorCode.DUPLICATE_DISPLAY_ORDER.getMessage())
                    .isEqualTo("동일한 표시 순서가 이미 존재합니다");
        }
    }

    @Nested
    @DisplayName("ACTIVE_COMMON_CODES_EXIST 테스트")
    class ActiveCommonCodesExistTest {

        @Test
        @DisplayName("에러 코드는 'CCT-004'이다")
        void hasCorrectCode() {
            assertThat(CommonCodeTypeErrorCode.ACTIVE_COMMON_CODES_EXIST.getCode())
                    .isEqualTo("CCT-004");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void hasCorrectHttpStatus() {
            assertThat(CommonCodeTypeErrorCode.ACTIVE_COMMON_CODES_EXIST.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("메시지는 '활성화된 공통 코드가 존재하여 비활성화할 수 없습니다'이다")
        void hasCorrectMessage() {
            assertThat(CommonCodeTypeErrorCode.ACTIVE_COMMON_CODES_EXIST.getMessage())
                    .isEqualTo("활성화된 공통 코드가 존재하여 비활성화할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(CommonCodeTypeErrorCode.values())
                    .containsExactly(
                            CommonCodeTypeErrorCode.NOT_FOUND,
                            CommonCodeTypeErrorCode.DUPLICATE_CODE,
                            CommonCodeTypeErrorCode.DUPLICATE_DISPLAY_ORDER,
                            CommonCodeTypeErrorCode.ACTIVE_COMMON_CODES_EXIST);
        }
    }
}
