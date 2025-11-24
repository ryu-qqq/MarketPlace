package com.ryuqq.marketplace.domain.externalmall;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ExternalMallStatus Enum 테스트
 *
 * <p>Red Phase: 실패하는 테스트 작성
 * - ExternalMallStatus Enum이 아직 존재하지 않음
 * - 컴파일 에러 발생 예상
 *
 * <p>검증 항목:
 * - 4개 상태 (PENDING, ACTIVE, INACTIVE, ERROR) 존재
 * - fromValue() 정적 메서드로 String → Enum 변환
 * - 상태 전환 규칙 검증 (isTransitionAllowedTo)
 *   - PENDING → ACTIVE만 허용
 *   - ACTIVE → INACTIVE만 허용
 */
@DisplayName("ExternalMallStatus Enum 테스트")
class ExternalMallStatusTest {

    @Nested
    @DisplayName("fromValue 메서드")
    class FromValueMethod {

        @ParameterizedTest
        @ValueSource(strings = {"PENDING", "ACTIVE", "INACTIVE", "ERROR"})
        @DisplayName("유효한 값으로 ExternalMallStatus Enum 반환")
        void shouldReturnStatusWhenValidValue(String value) {
            // When
            ExternalMallStatus result = ExternalMallStatus.fromValue(value);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("대소문자 구분 없이 변환 (pending → PENDING)")
        void shouldBeCaseInsensitive() {
            // When
            ExternalMallStatus result = ExternalMallStatus.fromValue("pending");

            // Then
            assertThat(result).isEqualTo(ExternalMallStatus.PENDING);
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "UNKNOWN", "DELETED"})
        @DisplayName("유효하지 않은 값은 IllegalArgumentException 발생")
        void shouldThrowExceptionWhenInvalidValue(String invalidValue) {
            // When & Then
            assertThatThrownBy(() -> ExternalMallStatus.fromValue(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 외부몰 상태");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 문자열은 IllegalArgumentException 발생")
        void shouldThrowExceptionWhenNullOrEmpty(String nullOrEmpty) {
            // When & Then
            assertThatThrownBy(() -> ExternalMallStatus.fromValue(nullOrEmpty))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Enum 값 검증")
    class EnumValueValidation {

        @Test
        @DisplayName("PENDING 상태 검증")
        void shouldHavePendingStatus() {
            // When
            ExternalMallStatus pending = ExternalMallStatus.PENDING;

            // Then
            assertThat(pending.getValue()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("ACTIVE 상태 검증")
        void shouldHaveActiveStatus() {
            // When
            ExternalMallStatus active = ExternalMallStatus.ACTIVE;

            // Then
            assertThat(active.getValue()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상태 검증")
        void shouldHaveInactiveStatus() {
            // When
            ExternalMallStatus inactive = ExternalMallStatus.INACTIVE;

            // Then
            assertThat(inactive.getValue()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("ERROR 상태 검증")
        void shouldHaveErrorStatus() {
            // When
            ExternalMallStatus error = ExternalMallStatus.ERROR;

            // Then
            assertThat(error.getValue()).isEqualTo("ERROR");
        }

        @Test
        @DisplayName("전체 4개 상태 존재")
        void shouldHaveFourStatuses() {
            // When
            ExternalMallStatus[] values = ExternalMallStatus.values();

            // Then
            assertThat(values).hasSize(4);
            assertThat(values).containsExactlyInAnyOrder(
                    ExternalMallStatus.PENDING,
                    ExternalMallStatus.ACTIVE,
                    ExternalMallStatus.INACTIVE,
                    ExternalMallStatus.ERROR
            );
        }
    }

    @Nested
    @DisplayName("상태 전환 규칙")
    class StatusTransitionRules {

        @ParameterizedTest
        @CsvSource({
                "PENDING, ACTIVE, true",    // PENDING → ACTIVE 허용
                "ACTIVE, INACTIVE, true",   // ACTIVE → INACTIVE 허용
        })
        @DisplayName("허용된 상태 전환")
        void shouldAllowValidTransitions(ExternalMallStatus from, ExternalMallStatus to, boolean expected) {
            // When
            boolean result = from.isTransitionAllowedTo(to);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
                "PENDING, INACTIVE",  // PENDING → INACTIVE 불가
                "PENDING, ERROR",     // PENDING → ERROR 불가
                "ACTIVE, PENDING",    // ACTIVE → PENDING 불가
                "ACTIVE, ACTIVE",     // ACTIVE → ACTIVE 불가 (동일 상태)
                "ACTIVE, ERROR",      // ACTIVE → ERROR 불가
                "INACTIVE, PENDING",  // INACTIVE → PENDING 불가
                "INACTIVE, ACTIVE",   // INACTIVE → ACTIVE 불가
                "INACTIVE, ERROR",    // INACTIVE → ERROR 불가
                "ERROR, PENDING",     // ERROR → PENDING 불가
                "ERROR, ACTIVE",      // ERROR → ACTIVE 불가
                "ERROR, INACTIVE",    // ERROR → INACTIVE 불가
        })
        @DisplayName("불가능한 상태 전환")
        void shouldNotAllowInvalidTransitions(ExternalMallStatus from, ExternalMallStatus to) {
            // When
            boolean result = from.isTransitionAllowedTo(to);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("PENDING → ACTIVE는 항상 허용")
        void shouldAlwaysAllowPendingToActive() {
            // When
            boolean result = ExternalMallStatus.PENDING.isTransitionAllowedTo(ExternalMallStatus.ACTIVE);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ACTIVE → INACTIVE는 항상 허용")
        void shouldAlwaysAllowActiveToInactive() {
            // When
            boolean result = ExternalMallStatus.ACTIVE.isTransitionAllowedTo(ExternalMallStatus.INACTIVE);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("동일 상태로의 전환은 불가")
        void shouldNotAllowSameStatusTransition() {
            // When & Then
            assertThat(ExternalMallStatus.PENDING.isTransitionAllowedTo(ExternalMallStatus.PENDING)).isFalse();
            assertThat(ExternalMallStatus.ACTIVE.isTransitionAllowedTo(ExternalMallStatus.ACTIVE)).isFalse();
            assertThat(ExternalMallStatus.INACTIVE.isTransitionAllowedTo(ExternalMallStatus.INACTIVE)).isFalse();
            assertThat(ExternalMallStatus.ERROR.isTransitionAllowedTo(ExternalMallStatus.ERROR)).isFalse();
        }
    }
}
