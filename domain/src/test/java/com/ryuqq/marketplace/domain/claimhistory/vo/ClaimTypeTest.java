package com.ryuqq.marketplace.domain.claimhistory.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimType enum 단위 테스트")
class ClaimTypeTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 ClaimType 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ClaimType.values())
                    .containsExactly(ClaimType.CANCEL, ClaimType.REFUND, ClaimType.EXCHANGE);
        }

        @Test
        @DisplayName("CANCEL의 description은 '취소'이다")
        void cancelDescription() {
            // then
            assertThat(ClaimType.CANCEL.description()).isEqualTo("취소");
        }

        @Test
        @DisplayName("REFUND의 description은 '환불'이다")
        void refundDescription() {
            // then
            assertThat(ClaimType.REFUND.description()).isEqualTo("환불");
        }

        @Test
        @DisplayName("EXCHANGE의 description은 '교환'이다")
        void exchangeDescription() {
            // then
            assertThat(ClaimType.EXCHANGE.description()).isEqualTo("교환");
        }
    }

    @Nested
    @DisplayName("enum 기본 동작 테스트")
    class EnumBehaviorTest {

        @Test
        @DisplayName("name()으로 enum 이름을 반환한다")
        void nameReturnsEnumName() {
            // then
            assertThat(ClaimType.CANCEL.name()).isEqualTo("CANCEL");
            assertThat(ClaimType.REFUND.name()).isEqualTo("REFUND");
            assertThat(ClaimType.EXCHANGE.name()).isEqualTo("EXCHANGE");
        }

        @Test
        @DisplayName("valueOf()로 enum 값을 조회한다")
        void valueOfReturnsEnum() {
            // then
            assertThat(ClaimType.valueOf("CANCEL")).isEqualTo(ClaimType.CANCEL);
            assertThat(ClaimType.valueOf("REFUND")).isEqualTo(ClaimType.REFUND);
            assertThat(ClaimType.valueOf("EXCHANGE")).isEqualTo(ClaimType.EXCHANGE);
        }
    }
}
