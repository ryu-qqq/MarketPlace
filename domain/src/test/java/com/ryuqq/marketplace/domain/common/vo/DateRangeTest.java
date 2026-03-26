package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DateRange Value Object 단위 테스트")
class DateRangeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("시작일과 종료일로 DateRange를 생성한다")
        void createWithStartAndEnd() {
            LocalDate start = LocalDate.of(2024, 1, 1);
            LocalDate end = LocalDate.of(2024, 12, 31);

            DateRange range = DateRange.of(start, end);

            assertThat(range.startDate()).isEqualTo(start);
            assertThat(range.endDate()).isEqualTo(end);
        }

        @Test
        @DisplayName("시작일과 종료일이 같아도 생성된다")
        void createWithSameDates() {
            LocalDate date = LocalDate.of(2024, 6, 15);

            DateRange range = DateRange.of(date, date);

            assertThat(range.startDate()).isEqualTo(date);
            assertThat(range.endDate()).isEqualTo(date);
        }

        @Test
        @DisplayName("시작일이 종료일보다 이후이면 예외가 발생한다")
        void createWithStartAfterEndThrowsException() {
            LocalDate start = LocalDate.of(2024, 12, 31);
            LocalDate end = LocalDate.of(2024, 1, 1);

            assertThatThrownBy(() -> DateRange.of(start, end))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이전");
        }

        @Test
        @DisplayName("시작일 null로 생성한다 (제한 없음)")
        void createWithNullStartDate() {
            LocalDate end = LocalDate.of(2024, 12, 31);

            DateRange range = DateRange.until(end);

            assertThat(range.startDate()).isNull();
            assertThat(range.endDate()).isEqualTo(end);
        }

        @Test
        @DisplayName("종료일 null로 생성한다 (제한 없음)")
        void createWithNullEndDate() {
            LocalDate start = LocalDate.of(2024, 1, 1);

            DateRange range = DateRange.from(start);

            assertThat(range.startDate()).isEqualTo(start);
            assertThat(range.endDate()).isNull();
        }
    }

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryTest {

        @Test
        @DisplayName("lastDays(7)은 최근 7일 범위를 반환한다")
        void lastDaysReturnsCorrectRange() {
            DateRange range = DateRange.lastDays(7);

            LocalDate today = LocalDate.now();
            assertThat(range.startDate()).isEqualTo(today.minusDays(7));
            assertThat(range.endDate()).isEqualTo(today);
        }

        @Test
        @DisplayName("lastDays(0)은 오늘만 포함하는 범위를 반환한다")
        void lastDaysZeroReturnsToday() {
            DateRange range = DateRange.lastDays(0);

            LocalDate today = LocalDate.now();
            assertThat(range.startDate()).isEqualTo(today);
            assertThat(range.endDate()).isEqualTo(today);
        }

        @Test
        @DisplayName("lastDays에 음수를 넣으면 예외가 발생한다")
        void lastDaysNegativeThrowsException() {
            assertThatThrownBy(() -> DateRange.lastDays(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0 이상");
        }

        @Test
        @DisplayName("thisMonth()는 이번 달 1일부터 말일까지를 반환한다")
        void thisMonthReturnsCurrentMonth() {
            DateRange range = DateRange.thisMonth();

            LocalDate today = LocalDate.now();
            assertThat(range.startDate()).isEqualTo(today.withDayOfMonth(1));
            assertThat(range.endDate()).isEqualTo(today.withDayOfMonth(today.lengthOfMonth()));
        }

        @Test
        @DisplayName("lastMonth()는 지난 달 범위를 반환한다")
        void lastMonthReturnsPreviousMonth() {
            DateRange range = DateRange.lastMonth();

            LocalDate today = LocalDate.now();
            LocalDate firstDayLastMonth = today.minusMonths(1).withDayOfMonth(1);
            LocalDate lastDayLastMonth = today.withDayOfMonth(1).minusDays(1);
            assertThat(range.startDate()).isEqualTo(firstDayLastMonth);
            assertThat(range.endDate()).isEqualTo(lastDayLastMonth);
        }
    }

    @Nested
    @DisplayName("isEmpty() 테스트")
    class IsEmptyTest {

        @Test
        @DisplayName("시작일과 종료일 모두 null이면 비어있다")
        void isEmptyWhenBothNull() {
            DateRange range = DateRange.of(null, null);

            assertThat(range.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("시작일이 있으면 비어있지 않다")
        void isNotEmptyWhenStartDateIsPresent() {
            DateRange range = DateRange.from(LocalDate.now());

            assertThat(range.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("종료일이 있으면 비어있지 않다")
        void isNotEmptyWhenEndDateIsPresent() {
            DateRange range = DateRange.until(LocalDate.now());

            assertThat(range.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("contains() 테스트")
    class ContainsTest {

        @Test
        @DisplayName("범위 내 날짜는 포함된다")
        void dateWithinRangeIsContained() {
            DateRange range = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            assertThat(range.contains(LocalDate.of(2024, 6, 15))).isTrue();
        }

        @Test
        @DisplayName("시작일은 범위에 포함된다")
        void startDateIsContained() {
            LocalDate start = LocalDate.of(2024, 1, 1);
            DateRange range = DateRange.of(start, LocalDate.of(2024, 12, 31));

            assertThat(range.contains(start)).isTrue();
        }

        @Test
        @DisplayName("종료일은 범위에 포함된다")
        void endDateIsContained() {
            LocalDate end = LocalDate.of(2024, 12, 31);
            DateRange range = DateRange.of(LocalDate.of(2024, 1, 1), end);

            assertThat(range.contains(end)).isTrue();
        }

        @Test
        @DisplayName("범위 밖 날짜는 포함되지 않는다")
        void dateOutsideRangeIsNotContained() {
            DateRange range = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            assertThat(range.contains(LocalDate.of(2025, 1, 1))).isFalse();
        }

        @Test
        @DisplayName("null은 포함되지 않는다")
        void nullDateIsNotContained() {
            DateRange range = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            assertThat(range.contains(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Instant 변환 테스트")
    class InstantConversionTest {

        @Test
        @DisplayName("시작일을 Instant로 변환한다")
        void startInstantIsNotNull() {
            DateRange range = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            assertThat(range.startInstant()).isNotNull();
        }

        @Test
        @DisplayName("시작일이 null이면 startInstant()는 null이다")
        void startInstantIsNullWhenStartDateIsNull() {
            DateRange range = DateRange.until(LocalDate.of(2024, 12, 31));

            assertThat(range.startInstant()).isNull();
        }

        @Test
        @DisplayName("종료일을 Instant로 변환한다")
        void endInstantIsNotNull() {
            DateRange range = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            assertThat(range.endInstant()).isNotNull();
        }

        @Test
        @DisplayName("종료일이 null이면 endInstant()는 null이다")
        void endInstantIsNullWhenEndDateIsNull() {
            DateRange range = DateRange.from(LocalDate.of(2024, 1, 1));

            assertThat(range.endInstant()).isNull();
        }
    }
}
