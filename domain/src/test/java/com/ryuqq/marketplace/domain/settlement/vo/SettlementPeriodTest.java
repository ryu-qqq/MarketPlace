package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementPeriod Value Object 단위 테스트")
class SettlementPeriodTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 기간으로 생성한다")
        void createWithValidPeriod() {
            LocalDate start = LocalDate.of(2025, 1, 1);
            LocalDate end = LocalDate.of(2025, 1, 7);

            SettlementPeriod period = SettlementPeriod.of(start, end, SettlementCycle.WEEKLY);

            assertThat(period.startDate()).isEqualTo(start);
            assertThat(period.endDate()).isEqualTo(end);
            assertThat(period.cycle()).isEqualTo(SettlementCycle.WEEKLY);
        }

        @Test
        @DisplayName("시작일과 종료일이 같아도 생성된다")
        void createWithSameDates() {
            LocalDate date = LocalDate.of(2025, 1, 1);

            SettlementPeriod period = SettlementPeriod.of(date, date, SettlementCycle.DAILY);

            assertThat(period.startDate()).isEqualTo(date);
            assertThat(period.endDate()).isEqualTo(date);
        }

        @Test
        @DisplayName("startDate가 null이면 예외가 발생한다")
        void throwWhenStartDateIsNull() {
            assertThatThrownBy(
                            () ->
                                    SettlementPeriod.of(
                                            null, LocalDate.now(), SettlementCycle.WEEKLY))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startDate");
        }

        @Test
        @DisplayName("endDate가 null이면 예외가 발생한다")
        void throwWhenEndDateIsNull() {
            assertThatThrownBy(
                            () ->
                                    SettlementPeriod.of(
                                            LocalDate.now(), null, SettlementCycle.WEEKLY))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("endDate");
        }

        @Test
        @DisplayName("cycle이 null이면 예외가 발생한다")
        void throwWhenCycleIsNull() {
            LocalDate start = LocalDate.now().minusDays(7);
            LocalDate end = LocalDate.now();

            assertThatThrownBy(() -> SettlementPeriod.of(start, end, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cycle");
        }

        @Test
        @DisplayName("startDate가 endDate 이후이면 예외가 발생한다")
        void throwWhenStartDateIsAfterEndDate() {
            LocalDate start = LocalDate.of(2025, 1, 8);
            LocalDate end = LocalDate.of(2025, 1, 1);

            assertThatThrownBy(() -> SettlementPeriod.of(start, end, SettlementCycle.WEEKLY))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startDate");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            LocalDate start = LocalDate.of(2025, 3, 1);
            LocalDate end = LocalDate.of(2025, 3, 7);

            SettlementPeriod period1 = SettlementPeriod.of(start, end, SettlementCycle.WEEKLY);
            SettlementPeriod period2 = SettlementPeriod.of(start, end, SettlementCycle.WEEKLY);

            assertThat(period1).isEqualTo(period2);
            assertThat(period1.hashCode()).isEqualTo(period2.hashCode());
        }

        @Test
        @DisplayName("다른 주기면 다르다")
        void differentCycleAreNotEqual() {
            LocalDate start = LocalDate.of(2025, 3, 1);
            LocalDate end = LocalDate.of(2025, 3, 7);

            SettlementPeriod weekly = SettlementPeriod.of(start, end, SettlementCycle.WEEKLY);
            SettlementPeriod daily = SettlementPeriod.of(start, end, SettlementCycle.DAILY);

            assertThat(weekly).isNotEqualTo(daily);
        }
    }
}
