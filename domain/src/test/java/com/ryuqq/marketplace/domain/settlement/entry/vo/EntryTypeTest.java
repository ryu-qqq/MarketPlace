package com.ryuqq.marketplace.domain.settlement.entry.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("EntryType 단위 테스트")
class EntryTypeTest {

    @Nested
    @DisplayName("isReversal() 테스트")
    class IsReversalTest {

        @Test
        @DisplayName("CANCEL은 역분개 유형이다")
        void cancelIsReversal() {
            assertThat(EntryType.CANCEL.isReversal()).isTrue();
        }

        @Test
        @DisplayName("REFUND는 역분개 유형이다")
        void refundIsReversal() {
            assertThat(EntryType.REFUND.isReversal()).isTrue();
        }

        @Test
        @DisplayName("EXCHANGE_OUT은 역분개 유형이다")
        void exchangeOutIsReversal() {
            assertThat(EntryType.EXCHANGE_OUT.isReversal()).isTrue();
        }

        @Test
        @DisplayName("SALES는 역분개 유형이 아니다")
        void salesIsNotReversal() {
            assertThat(EntryType.SALES.isReversal()).isFalse();
        }

        @Test
        @DisplayName("EXCHANGE_IN은 역분개 유형이 아니다")
        void exchangeInIsNotReversal() {
            assertThat(EntryType.EXCHANGE_IN.isReversal()).isFalse();
        }

        @Test
        @DisplayName("ADJUSTMENT는 역분개 유형이 아니다")
        void adjustmentIsNotReversal() {
            assertThat(EntryType.ADJUSTMENT.isReversal()).isFalse();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("EntryType은 6가지 값이다")
        void entryTypeValues() {
            EntryType[] values = EntryType.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            EntryType.SALES,
                            EntryType.CANCEL,
                            EntryType.REFUND,
                            EntryType.EXCHANGE_OUT,
                            EntryType.EXCHANGE_IN,
                            EntryType.ADJUSTMENT);
        }
    }
}
