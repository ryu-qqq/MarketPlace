package com.ryuqq.marketplace.domain.selleradmin.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("LoginId Value Object 단위 테스트")
class LoginIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 이메일 형식으로 생성한다")
        void createWithEmailFormat() {
            LoginId loginId = LoginId.of("admin@test.com");

            assertThat(loginId.value()).isEqualTo("admin@test.com");
        }

        @Test
        @DisplayName("유효한 일반 아이디 형식으로 생성한다")
        void createWithGeneralFormat() {
            LoginId loginId = LoginId.of("admin123");

            assertThat(loginId.value()).isEqualTo("admin123");
        }

        @Test
        @DisplayName("앞뒤 공백은 trim된다")
        void createWithWhitespaceTrimmed() {
            LoginId loginId = LoginId.of("  admin@test.com  ");

            assertThat(loginId.value()).isEqualTo("admin@test.com");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> LoginId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> LoginId.of("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            assertThatThrownBy(() -> LoginId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("3자 이하이면 예외가 발생한다")
        void createWithTooShort_ThrowsException() {
            assertThatThrownBy(() -> LoginId.of("abc"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("4자");
        }

        @Test
        @DisplayName("100자 초과이면 예외가 발생한다")
        void createWithTooLong_ThrowsException() {
            String longId = "a".repeat(101);
            assertThatThrownBy(() -> LoginId.of(longId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @ParameterizedTest
        @ValueSource(strings = {"한글아이디", "test!@", "test##"})
        @DisplayName("허용되지 않는 특수문자가 포함되면 예외가 발생한다")
        void createWithInvalidChars_ThrowsException(String invalidId) {
            assertThatThrownBy(() -> LoginId.of(invalidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }
    }

    @Nested
    @DisplayName("isEmailFormat() 테스트")
    class IsEmailFormatTest {

        @Test
        @DisplayName("이메일 형식이면 true를 반환한다")
        void returnsTrueForEmailFormat() {
            LoginId loginId = LoginId.of("admin@test.com");

            assertThat(loginId.isEmailFormat()).isTrue();
        }

        @Test
        @DisplayName("이메일 형식이 아니면 false를 반환한다")
        void returnsFalseForNonEmailFormat() {
            LoginId loginId = LoginId.of("admin123");

            assertThat(loginId.isEmailFormat()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            LoginId id1 = LoginId.of("admin@test.com");
            LoginId id2 = LoginId.of("admin@test.com");

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            LoginId id1 = LoginId.of("admin@test.com");
            LoginId id2 = LoginId.of("other@test.com");

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
