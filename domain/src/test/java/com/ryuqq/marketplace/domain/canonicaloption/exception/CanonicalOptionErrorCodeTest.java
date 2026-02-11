package com.ryuqq.marketplace.domain.canonicaloption.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionErrorCode 단위 테스트")
class CanonicalOptionErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("캐노니컬 옵션 그룹 관련 에러 코드 테스트")
    class CanonicalOptionGroupErrorCodesTest {

        @Test
        @DisplayName("CANONICAL_OPTION_GROUP_NOT_FOUND 에러 코드를 검증한다")
        void canonicalOptionGroupNotFound() {
            // then
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND.getCode())
                    .isEqualTo("CANONICAL_OPTION-001");
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND.getMessage())
                    .isEqualTo("캐노니컬 옵션 그룹을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("캐노니컬 옵션 값 관련 에러 코드 테스트")
    class CanonicalOptionValueErrorCodesTest {

        @Test
        @DisplayName("CANONICAL_OPTION_VALUE_NOT_FOUND 에러 코드를 검증한다")
        void canonicalOptionValueNotFound() {
            // then
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_VALUE_NOT_FOUND.getCode())
                    .isEqualTo("CANONICAL_OPTION-002");
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_VALUE_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(CanonicalOptionErrorCode.CANONICAL_OPTION_VALUE_NOT_FOUND.getMessage())
                    .isEqualTo("캐노니컬 옵션 값을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(CanonicalOptionErrorCode.values())
                    .containsExactly(
                            CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND,
                            CanonicalOptionErrorCode.CANONICAL_OPTION_VALUE_NOT_FOUND);
        }
    }
}
