package com.ryuqq.marketplace.domain.settlement.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementId 단위 테스트")
class SettlementIdTest {

    private static final String VALID_UUID = "01900000-0000-7000-8000-000000000001";

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 문자열 값으로 ID를 생성한다")
        void createWithValidValue() {
            // when
            SettlementId id = SettlementId.of(VALID_UUID);

            // then
            assertThat(id.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> SettlementId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> SettlementId.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            assertThatThrownBy(() -> SettlementId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew는 주입받은 UUIDv7 값으로 ID를 생성한다")
        void createForNewWithUuid() {
            // when
            SettlementId id = SettlementId.forNew(VALID_UUID);

            // then
            assertThat(id.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("forNew에 null을 전달하면 예외가 발생한다")
        void forNewWithNull_ThrowsException() {
            assertThatThrownBy(() -> SettlementId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 SettlementId는 같다")
        void sameValuesAreEqual() {
            // given
            SettlementId id1 = SettlementId.of(VALID_UUID);
            SettlementId id2 = SettlementId.of(VALID_UUID);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 SettlementId는 같지 않다")
        void differentValuesAreNotEqual() {
            // given
            SettlementId id1 = SettlementId.of(VALID_UUID);
            SettlementId id2 = SettlementId.of("01900000-0000-7000-8000-000000000002");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
