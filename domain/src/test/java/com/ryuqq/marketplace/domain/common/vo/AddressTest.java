package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Address Value Object 단위 테스트")
class AddressTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("우편번호, 기본주소, 상세주소로 Address를 생성한다")
        void createAddressWithAllFields() {
            Address address = Address.of("12345", "서울시 강남구", "101호");

            assertThat(address.zipcode()).isEqualTo("12345");
            assertThat(address.line1()).isEqualTo("서울시 강남구");
            assertThat(address.line2()).isEqualTo("101호");
        }

        @Test
        @DisplayName("상세주소 없이 Address를 생성한다")
        void createAddressWithoutLine2() {
            Address address = Address.of("12345", "서울시 강남구");

            assertThat(address.zipcode()).isEqualTo("12345");
            assertThat(address.line1()).isEqualTo("서울시 강남구");
            assertThat(address.line2()).isNull();
        }

        @Test
        @DisplayName("우편번호가 null이면 예외가 발생한다")
        void createWithNullZipcodeThrowsException() {
            assertThatThrownBy(() -> Address.of(null, "서울시 강남구"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("우편번호");
        }

        @Test
        @DisplayName("우편번호가 공백이면 예외가 발생한다")
        void createWithBlankZipcodeThrowsException() {
            assertThatThrownBy(() -> Address.of("  ", "서울시 강남구"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("우편번호");
        }

        @Test
        @DisplayName("기본주소가 null이면 예외가 발생한다")
        void createWithNullLine1ThrowsException() {
            assertThatThrownBy(() -> Address.of("12345", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("기본 주소");
        }

        @Test
        @DisplayName("기본주소가 공백이면 예외가 발생한다")
        void createWithBlankLine1ThrowsException() {
            assertThatThrownBy(() -> Address.of("12345", "  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("기본 주소");
        }

        @Test
        @DisplayName("우편번호 앞뒤 공백이 제거된다")
        void zipcodeIsTrimmed() {
            Address address = Address.of("  12345  ", "서울시 강남구");

            assertThat(address.zipcode()).isEqualTo("12345");
        }

        @Test
        @DisplayName("기본주소 앞뒤 공백이 제거된다")
        void line1IsTrimmed() {
            Address address = Address.of("12345", "  서울시 강남구  ");

            assertThat(address.line1()).isEqualTo("서울시 강남구");
        }
    }

    @Nested
    @DisplayName("fullAddress() 테스트")
    class FullAddressTest {

        @Test
        @DisplayName("상세주소가 있으면 기본주소와 상세주소를 공백으로 이어 반환한다")
        void fullAddressWithLine2() {
            Address address = Address.of("12345", "서울시 강남구", "101호");

            assertThat(address.fullAddress()).isEqualTo("서울시 강남구 101호");
        }

        @Test
        @DisplayName("상세주소가 null이면 기본주소만 반환한다")
        void fullAddressWithoutLine2() {
            Address address = Address.of("12345", "서울시 강남구");

            assertThat(address.fullAddress()).isEqualTo("서울시 강남구");
        }

        @Test
        @DisplayName("상세주소가 공백이면 기본주소만 반환한다")
        void fullAddressWithBlankLine2() {
            Address address = Address.of("12345", "서울시 강남구", "");

            assertThat(address.fullAddress()).isEqualTo("서울시 강남구");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            Address a = Address.of("12345", "서울시 강남구", "101호");
            Address b = Address.of("12345", "서울시 강남구", "101호");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            Address a = Address.of("12345", "서울시 강남구", "101호");
            Address b = Address.of("99999", "부산시 해운대구", "202호");

            assertThat(a).isNotEqualTo(b);
        }
    }
}
