package com.ryuqq.marketplace.domain.exchange.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeReason Value Object 단위 테스트")
class ExchangeReasonTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 reasonType과 reasonDetail로 생성한다")
        void createWithValidParams() {
            // when
            ExchangeReason reason =
                    new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, "사이즈 교환 요청합니다");

            // then
            assertThat(reason.reasonType()).isEqualTo(ExchangeReasonType.SIZE_CHANGE);
            assertThat(reason.reasonDetail()).isEqualTo("사이즈 교환 요청합니다");
        }

        @Test
        @DisplayName("모든 ExchangeReasonType으로 생성할 수 있다")
        void createWithAllReasonTypes() {
            for (ExchangeReasonType type : ExchangeReasonType.values()) {
                ExchangeReason reason = new ExchangeReason(type, "교환 사유 상세 내용");
                assertThat(reason.reasonType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("reasonType이 null이면 예외가 발생한다")
        void createWithNullReasonType_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeReason(null, "사유 상세"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 사유 유형은 null일 수 없습니다");
        }

        @Test
        @DisplayName("reasonDetail이 null이면 예외가 발생한다")
        void createWithNullReasonDetail_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 사유 상세는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("reasonDetail이 빈 문자열이면 예외가 발생한다")
        void createWithBlankReasonDetail_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 사유 상세는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("reasonDetail이 공백만 있으면 예외가 발생한다")
        void createWithWhitespaceReasonDetail_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 사유 상세는 null 또는 빈 문자열일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            ExchangeReason reason1 =
                    new ExchangeReason(ExchangeReasonType.DEFECTIVE, "상품 불량으로 교환 요청합니다");
            ExchangeReason reason2 =
                    new ExchangeReason(ExchangeReasonType.DEFECTIVE, "상품 불량으로 교환 요청합니다");

            // then
            assertThat(reason1).isEqualTo(reason2);
            assertThat(reason1.hashCode()).isEqualTo(reason2.hashCode());
        }

        @Test
        @DisplayName("reasonType이 다르면 동일하지 않다")
        void differentReasonTypeIsNotEqual() {
            // given
            ExchangeReason reason1 = new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, "교환 요청");
            ExchangeReason reason2 = new ExchangeReason(ExchangeReasonType.DEFECTIVE, "교환 요청");

            // then
            assertThat(reason1).isNotEqualTo(reason2);
        }

        @Test
        @DisplayName("reasonDetail이 다르면 동일하지 않다")
        void differentReasonDetailIsNotEqual() {
            // given
            ExchangeReason reason1 =
                    new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, "사이즈 L로 교환 요청");
            ExchangeReason reason2 =
                    new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, "사이즈 XL로 교환 요청");

            // then
            assertThat(reason1).isNotEqualTo(reason2);
        }
    }

    @Nested
    @DisplayName("Fixtures 기반 생성 테스트")
    class FixturesTest {

        @Test
        @DisplayName("defaultExchangeReason이 올바르게 생성된다")
        void defaultExchangeReasonCreated() {
            // when
            ExchangeReason reason = ExchangeFixtures.defaultExchangeReason();

            // then
            assertThat(reason.reasonType()).isEqualTo(ExchangeReasonType.SIZE_CHANGE);
            assertThat(reason.reasonDetail()).isNotBlank();
        }
    }
}
