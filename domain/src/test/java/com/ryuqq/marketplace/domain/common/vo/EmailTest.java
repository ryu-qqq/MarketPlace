package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Email Value Object 단위 테스트")
class EmailTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 이메일로 Email을 생성한다")
        void createWithValidEmail() {
            Email email = Email.of("test@example.com");

            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("이메일은 소문자로 정규화된다")
        void emailIsNormalizedToLowercase() {
            Email email = Email.of("TEST@EXAMPLE.COM");

            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("이메일 앞뒤 공백이 제거된다")
        void emailIsTrimmed() {
            Email email = Email.of("  test@example.com  ");

            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> Email.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이메일");
        }

        @Test
        @DisplayName("공백이면 예외가 발생한다")
        void createWithBlankThrowsException() {
            assertThatThrownBy(() -> Email.of("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이메일");
        }

        @Test
        @DisplayName("@ 없는 형식이면 예외가 발생한다")
        void createWithoutAtSignThrowsException() {
            assertThatThrownBy(() -> Email.of("invalidemail"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }

        @Test
        @DisplayName("도메인 없는 형식이면 예외가 발생한다")
        void createWithoutDomainThrowsException() {
            assertThatThrownBy(() -> Email.of("test@"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }

        @Test
        @DisplayName("TLD가 너무 짧으면 예외가 발생한다")
        void createWithShortTldThrowsException() {
            assertThatThrownBy(() -> Email.of("test@example.c"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }
    }

    @Nested
    @DisplayName("파싱 테스트")
    class ParseTest {

        @Test
        @DisplayName("로컬 파트를 반환한다")
        void localPartIsReturnedCorrectly() {
            Email email = Email.of("test@example.com");

            assertThat(email.localPart()).isEqualTo("test");
        }

        @Test
        @DisplayName("도메인 파트를 반환한다")
        void domainPartIsReturnedCorrectly() {
            Email email = Email.of("test@example.com");

            assertThat(email.domainPart()).isEqualTo("example.com");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 이메일은 동일하다")
        void sameEmailIsEqual() {
            Email a = Email.of("test@example.com");
            Email b = Email.of("test@example.com");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("대소문자 다른 이메일도 정규화 후 동일하다")
        void emailsAreEqualAfterNormalization() {
            Email a = Email.of("TEST@EXAMPLE.COM");
            Email b = Email.of("test@example.com");

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("다른 이메일은 동일하지 않다")
        void differentEmailIsNotEqual() {
            Email a = Email.of("test@example.com");
            Email b = Email.of("other@example.com");

            assertThat(a).isNotEqualTo(b);
        }
    }
}
