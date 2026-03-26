package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementCycle 단위 테스트")
class SettlementCycleTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("SettlementCycle은 4가지 값이다")
        void cycleValues() {
            SettlementCycle[] values = SettlementCycle.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementCycle.DAILY,
                            SettlementCycle.WEEKLY,
                            SettlementCycle.BIWEEKLY,
                            SettlementCycle.MONTHLY);
        }

        @Test
        @DisplayName("DAILY는 일간 정산 주기이다")
        void dailyCycle() {
            assertThat(SettlementCycle.valueOf("DAILY")).isEqualTo(SettlementCycle.DAILY);
        }

        @Test
        @DisplayName("WEEKLY는 주간 정산 주기이다")
        void weeklyCycle() {
            assertThat(SettlementCycle.valueOf("WEEKLY")).isEqualTo(SettlementCycle.WEEKLY);
        }

        @Test
        @DisplayName("BIWEEKLY는 격주 정산 주기이다")
        void biweeklyCycle() {
            assertThat(SettlementCycle.valueOf("BIWEEKLY")).isEqualTo(SettlementCycle.BIWEEKLY);
        }

        @Test
        @DisplayName("MONTHLY는 월간 정산 주기이다")
        void monthlyCycle() {
            assertThat(SettlementCycle.valueOf("MONTHLY")).isEqualTo(SettlementCycle.MONTHLY);
        }
    }
}
