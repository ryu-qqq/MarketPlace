package com.ryuqq.marketplace.domain.canonicaloption.exception;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionException 단위 테스트")
class CanonicalOptionExceptionTest {

    @Nested
    @DisplayName("예외 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            CanonicalOptionException exception =
                    new CanonicalOptionException(
                            CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("캐노니컬 옵션 그룹을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            String customMessage = "ID 999인 캐노니컬 옵션 그룹을 찾을 수 없습니다";

            // when
            CanonicalOptionException exception =
                    new CanonicalOptionException(
                            CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND, customMessage);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            Throwable cause = new RuntimeException("원인 예외");

            // when
            CanonicalOptionException exception =
                    new CanonicalOptionException(
                            CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND, cause);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("CanonicalOptionGroupNotFoundException 테스트")
    class CanonicalOptionGroupNotFoundExceptionTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            CanonicalOptionGroupNotFoundException exception =
                    new CanonicalOptionGroupNotFoundException();

            // then
            assertThat(exception).isInstanceOf(CanonicalOptionException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("캐노니컬 옵션 그룹을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("ID를 포함한 메시지로 예외를 생성한다")
        void createWithId() {
            // given
            Long id = 999L;

            // when
            CanonicalOptionGroupNotFoundException exception =
                    new CanonicalOptionGroupNotFoundException(id);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);
            assertThat(exception.getMessage()).contains("999");
        }
    }

    @Nested
    @DisplayName("예외 던지기 테스트")
    class ThrowingTest {

        @Test
        @DisplayName("CanonicalOptionException을 던질 수 있다")
        void canThrowCanonicalOptionException() {
            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new CanonicalOptionException(
                                        CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND);
                            })
                    .isInstanceOf(CanonicalOptionException.class)
                    .hasMessage("캐노니컬 옵션 그룹을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CanonicalOptionGroupNotFoundException을 던질 수 있다")
        void canThrowCanonicalOptionGroupNotFoundException() {
            // when & then
            assertThatThrownBy(() -> {
                throw new CanonicalOptionGroupNotFoundException(123L);
            })
                    .isInstanceOf(CanonicalOptionGroupNotFoundException.class)
                    .isInstanceOf(CanonicalOptionException.class)
                    .isInstanceOf(DomainException.class);
        }
    }
}
