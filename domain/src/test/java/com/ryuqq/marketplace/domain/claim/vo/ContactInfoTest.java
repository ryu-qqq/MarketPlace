package com.ryuqq.marketplace.domain.claim.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ContactInfo Value Object 단위 테스트")
class ContactInfoTest {

    private static final String DEFAULT_NAME = "홍길동";
    private static final String DEFAULT_PHONE = "010-1234-5678";
    private static final Address DEFAULT_ADDRESS = Address.of("12345", "서울시 강남구 테헤란로 1", "101호");

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ContactInfo를 생성한다")
        void createWithValidValues() {
            // given & when
            ContactInfo contactInfo = ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_ADDRESS);

            // then
            assertThat(contactInfo.name()).isEqualTo(DEFAULT_NAME);
            assertThat(contactInfo.phone()).isEqualTo(DEFAULT_PHONE);
            assertThat(contactInfo.address()).isEqualTo(DEFAULT_ADDRESS);
        }

        @Test
        @DisplayName("이름이 null이면 예외가 발생한다")
        void createWithNullName_ThrowsException() {
            assertThatThrownBy(() -> ContactInfo.of(null, DEFAULT_PHONE, DEFAULT_ADDRESS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이름은 필수");
        }

        @Test
        @DisplayName("이름이 빈 문자열이면 예외가 발생한다")
        void createWithBlankName_ThrowsException() {
            assertThatThrownBy(() -> ContactInfo.of("  ", DEFAULT_PHONE, DEFAULT_ADDRESS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이름은 필수");
        }

        @Test
        @DisplayName("연락처가 null이면 예외가 발생한다")
        void createWithNullPhone_ThrowsException() {
            assertThatThrownBy(() -> ContactInfo.of(DEFAULT_NAME, null, DEFAULT_ADDRESS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("연락처는 필수");
        }

        @Test
        @DisplayName("연락처가 빈 문자열이면 예외가 발생한다")
        void createWithBlankPhone_ThrowsException() {
            assertThatThrownBy(() -> ContactInfo.of(DEFAULT_NAME, "   ", DEFAULT_ADDRESS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("연락처는 필수");
        }

        @Test
        @DisplayName("주소가 null이면 예외가 발생한다")
        void createWithNullAddress_ThrowsException() {
            assertThatThrownBy(() -> ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주소는 필수");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            ContactInfo contactInfo1 = ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_ADDRESS);
            ContactInfo contactInfo2 = ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_ADDRESS);

            // then
            assertThat(contactInfo1).isEqualTo(contactInfo2);
            assertThat(contactInfo1.hashCode()).isEqualTo(contactInfo2.hashCode());
        }

        @Test
        @DisplayName("이름이 다르면 동일하지 않다")
        void differentNameAreNotEqual() {
            // given
            ContactInfo contactInfo1 = ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_ADDRESS);
            ContactInfo contactInfo2 = ContactInfo.of("김철수", DEFAULT_PHONE, DEFAULT_ADDRESS);

            // then
            assertThat(contactInfo1).isNotEqualTo(contactInfo2);
        }

        @Test
        @DisplayName("연락처가 다르면 동일하지 않다")
        void differentPhoneAreNotEqual() {
            // given
            ContactInfo contactInfo1 = ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_ADDRESS);
            ContactInfo contactInfo2 =
                    ContactInfo.of(DEFAULT_NAME, "010-9999-8888", DEFAULT_ADDRESS);

            // then
            assertThat(contactInfo1).isNotEqualTo(contactInfo2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ContactInfo는 record이므로 불변이다")
        void contactInfoIsImmutable() {
            // given
            ContactInfo contactInfo = ContactInfo.of(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_ADDRESS);

            // then
            assertThat(contactInfo.name()).isEqualTo(DEFAULT_NAME);
            assertThat(contactInfo.phone()).isEqualTo(DEFAULT_PHONE);
            assertThat(contactInfo.address()).isEqualTo(DEFAULT_ADDRESS);
        }
    }
}
