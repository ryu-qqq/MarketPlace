package com.ryuqq.marketplace.domain.refund.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundClaimId Value Object 단위 테스트")
class RefundClaimIdTest {

    @Nested
    @DisplayName("of() / forNew() - 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 문자열로 ID를 생성한다")
        void createWithValidValue() {
            // when
            RefundClaimId id = RefundClaimId.of("REFUND-CLAIM-0001");

            // then
            assertThat(id.value()).isEqualTo("REFUND-CLAIM-0001");
        }

        @Test
        @DisplayName("forNew()로 ID를 생성한다")
        void createWithForNew() {
            // when
            RefundClaimId id = RefundClaimId.forNew("REFUND-CLAIM-NEW-001");

            // then
            assertThat(id.value()).isEqualTo("REFUND-CLAIM-NEW-001");
        }

        @Test
        @DisplayName("value가 null이면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundClaimId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("value가 빈 문자열이면 예외가 발생한다")
        void createWithBlankValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundClaimId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            RefundClaimId id1 = RefundClaimId.of("REFUND-CLAIM-0001");
            RefundClaimId id2 = RefundClaimId.of("REFUND-CLAIM-0001");

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            RefundClaimId id1 = RefundClaimId.of("REFUND-CLAIM-0001");
            RefundClaimId id2 = RefundClaimId.of("REFUND-CLAIM-0002");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
