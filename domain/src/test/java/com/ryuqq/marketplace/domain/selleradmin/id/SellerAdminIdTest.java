package com.ryuqq.marketplace.domain.selleradmin.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminId 단위 테스트")
class SellerAdminIdTest {

    private static final String VALID_UUID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 문자열 값으로 ID를 생성한다")
        void createWithValidValue() {
            SellerAdminId id = SellerAdminId.of(VALID_UUID);

            assertThat(id.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> SellerAdminId.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> SellerAdminId.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            assertThatThrownBy(() -> SellerAdminId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew는 주입받은 UUIDv7 값으로 ID를 생성한다")
        void createForNewWithUuid() {
            SellerAdminId id = SellerAdminId.forNew(VALID_UUID);

            assertThat(id.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("forNew에 null을 전달하면 예외가 발생한다")
        void forNewWithNull_ThrowsException() {
            assertThatThrownBy(() -> SellerAdminId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 SellerAdminId는 같다")
        void sameValuesAreEqual() {
            SellerAdminId id1 = SellerAdminId.of(VALID_UUID);
            SellerAdminId id2 = SellerAdminId.of(VALID_UUID);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 SellerAdminId는 같지 않다")
        void differentValuesAreNotEqual() {
            SellerAdminId id1 = SellerAdminId.of(VALID_UUID);
            SellerAdminId id2 = SellerAdminId.of("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f61");

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
