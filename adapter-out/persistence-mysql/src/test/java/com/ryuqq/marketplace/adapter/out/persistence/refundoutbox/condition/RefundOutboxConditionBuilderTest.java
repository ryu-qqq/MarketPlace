package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefundOutboxConditionBuilderTest - 환불 아웃박스 조건 빌더 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("RefundOutboxConditionBuilder 단위 테스트")
class RefundOutboxConditionBuilderTest {

    private RefundOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new RefundOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. statusPending 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusPending 메서드 테스트")
    class StatusPendingTest {

        @Test
        @DisplayName("statusPending 호출 시 BooleanExpression을 반환합니다")
        void statusPending_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusPending();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("statusPending 조건은 PENDING 상태를 필터링합니다")
        void statusPending_FiltersOnPendingStatus() {
            // when
            BooleanExpression result = conditionBuilder.statusPending();

            // then
            assertThat(result.toString()).contains("PENDING");
        }
    }

    // ========================================================================
    // 2. statusProcessing 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusProcessing 메서드 테스트")
    class StatusProcessingTest {

        @Test
        @DisplayName("statusProcessing 호출 시 BooleanExpression을 반환합니다")
        void statusProcessing_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusProcessing();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("statusProcessing 조건은 PROCESSING 상태를 필터링합니다")
        void statusProcessing_FiltersOnProcessingStatus() {
            // when
            BooleanExpression result = conditionBuilder.statusProcessing();

            // then
            assertThat(result.toString()).contains("PROCESSING");
        }
    }

    // ========================================================================
    // 3. createdAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtBefore 메서드 테스트")
    class CreatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간 입력 시 BooleanExpression을 반환합니다")
        void createdAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);

            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 시간 입력 시 null을 반환합니다")
        void createdAtBefore_WithNullTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. updatedAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("updatedAtBefore 메서드 테스트")
    class UpdatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간 입력 시 BooleanExpression을 반환합니다")
        void updatedAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);

            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 시간 입력 시 null을 반환합니다")
        void updatedAtBefore_WithNullTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("타임아웃 기준 시간으로 조건 생성이 가능합니다")
        void updatedAtBefore_WithTimeoutThreshold_ReturnsBooleanExpression() {
            // given - 5분 전 = 타임아웃 기준
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(timeoutThreshold);

            // then
            assertThat(result).isNotNull();
        }
    }
}
