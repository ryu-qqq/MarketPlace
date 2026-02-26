package com.ryuqq.marketplace.domain.exchange.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeClaimId Value Object 단위 테스트")
class ExchangeClaimIdTest {

    private static final String VALID_UUID = "01900000-0000-7000-0000-000000000001";

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 UUID 문자열로 생성한다")
        void createWithValidUuid() {
            // when
            ExchangeClaimId id = ExchangeClaimId.of(VALID_UUID);

            // then
            assertThat(id).isNotNull();
            assertThat(id.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeClaimId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeClaimId 값은 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithBlankValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeClaimId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeClaimId 값은 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있는 문자열로 생성하면 예외가 발생한다")
        void createWithWhitespaceValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeClaimId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeClaimId 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 생성 테스트")
    class ForNewTest {

        @Test
        @DisplayName("외부에서 주입받은 UUID로 신규 ID를 생성한다")
        void createForNewWithUuid() {
            // when
            ExchangeClaimId id = ExchangeClaimId.forNew(VALID_UUID);

            // then
            assertThat(id.value()).isEqualTo(VALID_UUID);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            ExchangeClaimId id1 = ExchangeClaimId.of(VALID_UUID);
            ExchangeClaimId id2 = ExchangeClaimId.of(VALID_UUID);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            ExchangeClaimId id1 = ExchangeClaimId.of(VALID_UUID);
            ExchangeClaimId id2 = ExchangeClaimId.of("01900000-0000-7000-0000-000000000002");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
