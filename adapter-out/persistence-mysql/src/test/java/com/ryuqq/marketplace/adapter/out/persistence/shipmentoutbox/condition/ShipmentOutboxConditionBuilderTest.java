package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ShipmentOutboxConditionBuilder 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ShipmentOutboxConditionBuilder 단위 테스트")
class ShipmentOutboxConditionBuilderTest {

    private ShipmentOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ShipmentOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. statusPending 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusPending 메서드 테스트")
    class StatusPendingTest {

        @Test
        @DisplayName("statusPending은 항상 BooleanExpression을 반환합니다")
        void statusPending_AlwaysReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusPending();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("statusPending을 여러 번 호출해도 일관된 결과를 반환합니다")
        void statusPending_CalledMultipleTimes_ReturnsConsistentResult() {
            // when
            BooleanExpression result1 = conditionBuilder.statusPending();
            BooleanExpression result2 = conditionBuilder.statusPending();

            // then
            assertThat(result1).isNotNull();
            assertThat(result2).isNotNull();
        }
    }

    // ========================================================================
    // 2. statusProcessing 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusProcessing 메서드 테스트")
    class StatusProcessingTest {

        @Test
        @DisplayName("statusProcessing은 항상 BooleanExpression을 반환합니다")
        void statusProcessing_AlwaysReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusProcessing();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("statusPending과 statusProcessing은 다른 BooleanExpression을 반환합니다")
        void statusProcessing_DifferentFromStatusPending() {
            // when
            BooleanExpression pending = conditionBuilder.statusPending();
            BooleanExpression processing = conditionBuilder.statusProcessing();

            // then
            assertThat(pending).isNotNull();
            assertThat(processing).isNotNull();
            assertThat(pending.toString()).isNotEqualTo(processing.toString());
        }
    }

    // ========================================================================
    // 3. createdAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtBefore 메서드 테스트")
    class CreatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간으로 조회 시 BooleanExpression을 반환합니다")
        void createdAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 시간으로 조회 시 null을 반환합니다")
        void createdAtBefore_WithNullTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("과거 시간으로 조회 시 BooleanExpression을 반환합니다")
        void createdAtBefore_WithPastTime_ReturnsBooleanExpression() {
            // given
            Instant pastTime = Instant.now().minusSeconds(3600);

            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(pastTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("미래 시간으로 조회 시 BooleanExpression을 반환합니다")
        void createdAtBefore_WithFutureTime_ReturnsBooleanExpression() {
            // given
            Instant futureTime = Instant.now().plusSeconds(3600);

            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(futureTime);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. updatedAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("updatedAtBefore 메서드 테스트")
    class UpdatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간으로 조회 시 BooleanExpression을 반환합니다")
        void updatedAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 시간으로 조회 시 null을 반환합니다")
        void updatedAtBefore_WithNullTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("타임아웃 기준 시간으로 BooleanExpression을 반환합니다")
        void updatedAtBefore_WithTimeoutBefore_ReturnsBooleanExpression() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);

            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(timeoutBefore);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("createdAtBefore와 updatedAtBefore는 다른 BooleanExpression을 반환합니다")
        void updatedAtBefore_DifferentFromCreatedAtBefore() {
            // given
            Instant now = Instant.now();

            // when
            BooleanExpression createdBefore = conditionBuilder.createdAtBefore(now);
            BooleanExpression updatedBefore = conditionBuilder.updatedAtBefore(now);

            // then
            assertThat(createdBefore).isNotNull();
            assertThat(updatedBefore).isNotNull();
            assertThat(createdBefore.toString()).isNotEqualTo(updatedBefore.toString());
        }
    }
}
