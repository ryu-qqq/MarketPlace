package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("PhoneNumber Value Object 단위 테스트")
class PhoneNumberTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("하이픈 포함 전화번호로 생성한다")
        void createWithHyphenFormat() {
            PhoneNumber phone = PhoneNumber.of("010-1234-5678");

            assertThat(phone.value()).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("숫자만 있는 전화번호로 생성한다")
        void createWithDigitsOnly() {
            PhoneNumber phone = PhoneNumber.of("01012345678");

            assertThat(phone.value()).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("앞뒤 공백이 제거된다")
        void phoneNumberIsTrimmed() {
            PhoneNumber phone = PhoneNumber.of("  010-1234-5678  ");

            assertThat(phone.value()).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> PhoneNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("전화번호");
        }

        @Test
        @DisplayName("공백이면 예외가 발생한다")
        void createWithBlankThrowsException() {
            assertThatThrownBy(() -> PhoneNumber.of("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("전화번호");
        }

        @Test
        @DisplayName("너무 짧은 번호이면 예외가 발생한다")
        void createWithTooShortNumberThrowsException() {
            assertThatThrownBy(() -> PhoneNumber.of("0101"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }

        @Test
        @DisplayName("너무 긴 번호이면 예외가 발생한다")
        void createWithTooLongNumberThrowsException() {
            assertThatThrownBy(() -> PhoneNumber.of("010-1234-5678-9999-0000"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }

        @Test
        @DisplayName("알파벳을 포함한 번호이면 예외가 발생한다")
        void createWithAlphabetThrowsException() {
            assertThatThrownBy(() -> PhoneNumber.of("010-ABCD-5678"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }
    }

    @Nested
    @DisplayName("digitsOnly() 테스트")
    class DigitsOnlyTest {

        @Test
        @DisplayName("하이픈이 제거된 숫자만 반환한다")
        void digitsOnlyRemovesHyphens() {
            PhoneNumber phone = PhoneNumber.of("010-1234-5678");

            assertThat(phone.digitsOnly()).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("이미 숫자만인 경우 그대로 반환한다")
        void digitsOnlyReturnsSameWhenNoHyphens() {
            PhoneNumber phone = PhoneNumber.of("01012345678");

            assertThat(phone.digitsOnly()).isEqualTo("01012345678");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 번호는 동일하다")
        void samePhoneNumberIsEqual() {
            PhoneNumber a = PhoneNumber.of("010-1234-5678");
            PhoneNumber b = PhoneNumber.of("010-1234-5678");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("다른 번호는 동일하지 않다")
        void differentPhoneNumberIsNotEqual() {
            PhoneNumber a = PhoneNumber.of("010-1234-5678");
            PhoneNumber b = PhoneNumber.of("010-9999-8888");

            assertThat(a).isNotEqualTo(b);
        }
    }
}
