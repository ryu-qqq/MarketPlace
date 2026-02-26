package com.ryuqq.marketplace.domain.seller.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("Seller Business VO 테스트")
class SellerBusinessVoTest {

    @Nested
    @DisplayName("RegistrationNumber 테스트")
    class RegistrationNumberTest {

        @Test
        @DisplayName("하이픈 포함 사업자등록번호를 생성한다")
        void createWithHyphen() {
            var regNo = RegistrationNumber.of("123-45-67890");
            assertThat(regNo.value()).isEqualTo("123-45-67890");
        }

        @Test
        @DisplayName("숫자만 입력하면 자동으로 형식이 변환된다")
        void autoFormatDigitsOnly() {
            var regNo = RegistrationNumber.of("1234567890");
            assertThat(regNo.value()).isEqualTo("123-45-67890");
        }

        @Test
        @DisplayName("digitsOnly()는 하이픈 없는 숫자를 반환한다")
        void returnsDigitsOnly() {
            var regNo = RegistrationNumber.of("123-45-67890");
            assertThat(regNo.digitsOnly()).isEqualTo("1234567890");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> RegistrationNumber.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"123-45-6789", "1234-56-78901", "abc-de-fghij"})
        @DisplayName("유효하지 않은 형식이면 예외가 발생한다")
        void throwExceptionForInvalidFormat(String value) {
            assertThatThrownBy(() -> RegistrationNumber.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("CompanyName 테스트")
    class CompanyNameTest {

        @Test
        @DisplayName("상호명을 생성한다")
        void createCompanyName() {
            var companyName = CompanyName.of("테스트 주식회사");
            assertThat(companyName.value()).isEqualTo("테스트 주식회사");
        }

        @Test
        @DisplayName("공백은 트림된다")
        void trimWhitespace() {
            var companyName = CompanyName.of("  테스트 주식회사  ");
            assertThat(companyName.value()).isEqualTo("테스트 주식회사");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> CompanyName.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("100자 초과 시 예외가 발생한다")
        void throwExceptionForTooLong() {
            String longName = "가".repeat(101);
            assertThatThrownBy(() -> CompanyName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100");
        }
    }

    @Nested
    @DisplayName("Representative 테스트")
    class RepresentativeTest {

        @Test
        @DisplayName("대표자명을 생성한다")
        void createRepresentative() {
            var rep = Representative.of("홍길동");
            assertThat(rep.value()).isEqualTo("홍길동");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> Representative.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("50자 초과 시 예외가 발생한다")
        void throwExceptionForTooLong() {
            String longName = "가".repeat(51);
            assertThatThrownBy(() -> Representative.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50");
        }
    }

    @Nested
    @DisplayName("ContactInfo 테스트")
    class ContactInfoTest {

        @Test
        @DisplayName("담당자 연락처를 생성한다")
        void createContactInfo() {
            var contactInfo = ContactInfo.of("홍길동", "010-1234-5678", "hong@test.com");

            assertThat(contactInfo.name()).isEqualTo("홍길동");
            assertThat(contactInfo.phoneValue()).isEqualTo("010-1234-5678");
            assertThat(contactInfo.emailValue()).isEqualTo("hong@test.com");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("담당자명이 null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmptyName(String value) {
            assertThatThrownBy(() -> ContactInfo.of(value, "010-1234-5678", "test@test.com"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("담당자명이 50자 초과 시 예외가 발생한다")
        void throwExceptionForTooLongName() {
            String longName = "가".repeat(51);
            assertThatThrownBy(() -> ContactInfo.of(longName, "010-1234-5678", "test@test.com"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("CsContact 테스트")
    class CsContactTest {

        @Test
        @DisplayName("전화번호만으로 CS 연락처를 생성한다")
        void createWithPhoneOnly() {
            var csContact = CsContact.of("02-1234-5678", null, null);

            assertThat(csContact.hasPhone()).isTrue();
            assertThat(csContact.hasMobile()).isFalse();
            assertThat(csContact.hasEmail()).isFalse();
            assertThat(csContact.phoneValue()).isEqualTo("02-1234-5678");
        }

        @Test
        @DisplayName("이메일만으로 CS 연락처를 생성한다")
        void createWithEmailOnly() {
            var csContact = CsContact.of(null, null, "cs@test.com");

            assertThat(csContact.hasEmail()).isTrue();
            assertThat(csContact.hasPhone()).isFalse();
            assertThat(csContact.hasMobile()).isFalse();
            assertThat(csContact.emailValue()).isEqualTo("cs@test.com");
        }

        @Test
        @DisplayName("모든 연락처 정보로 생성한다")
        void createWithAllContacts() {
            var csContact = CsContact.of("02-1234-5678", "010-1234-5678", "cs@test.com");

            assertThat(csContact.hasPhone()).isTrue();
            assertThat(csContact.hasMobile()).isTrue();
            assertThat(csContact.hasEmail()).isTrue();
        }

        @Test
        @DisplayName("모든 연락처가 null이면 예외가 발생한다")
        void throwExceptionForAllNull() {
            assertThatThrownBy(() -> CsContact.of(null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최소 하나");
        }

        @Test
        @DisplayName("모든 연락처가 빈 값이면 예외가 발생한다")
        void throwExceptionForAllEmpty() {
            assertThatThrownBy(() -> CsContact.of("", "", ""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
