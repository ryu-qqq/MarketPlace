package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyOrderConversionOutboxConditionBuilderTest - 주문 Outbox 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyOrderConversionOutboxConditionBuilder 단위 테스트")
class LegacyOrderConversionOutboxConditionBuilderTest {

    private LegacyOrderConversionOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new LegacyOrderConversionOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. statusPending 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusPending 메서드 테스트")
    class StatusPendingTest {

        @Test
        @DisplayName("PENDING 상태 조건 BooleanExpression을 반환합니다")
        void statusPending_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusPending();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 2. statusProcessing 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusProcessing 메서드 테스트")
    class StatusProcessingTest {

        @Test
        @DisplayName("PROCESSING 상태 조건 BooleanExpression을 반환합니다")
        void statusProcessing_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusProcessing();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 3. retryCountLtMaxRetry 테스트
    // ========================================================================

    @Nested
    @DisplayName("retryCountLtMaxRetry 메서드 테스트")
    class RetryCountLtMaxRetryTest {

        @Test
        @DisplayName("retryCount < maxRetry 조건 BooleanExpression을 반환합니다")
        void retryCountLtMaxRetry_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.retryCountLtMaxRetry();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. createdAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtBefore 메서드 테스트")
    class CreatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간 입력 시 BooleanExpression을 반환합니다")
        void createdAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void createdAtBefore_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. updatedAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("updatedAtBefore 메서드 테스트")
    class UpdatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간 입력 시 BooleanExpression을 반환합니다")
        void updatedAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void updatedAtBefore_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. legacyOrderIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("legacyOrderIdEq 메서드 테스트")
    class LegacyOrderIdEqTest {

        @Test
        @DisplayName("유효한 legacyOrderId 입력 시 BooleanExpression을 반환합니다")
        void legacyOrderIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long legacyOrderId = 10001L;

            // when
            BooleanExpression result = conditionBuilder.legacyOrderIdEq(legacyOrderId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void legacyOrderIdEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.legacyOrderIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. legacyOrderIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("legacyOrderIdIn 메서드 테스트")
    class LegacyOrderIdInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void legacyOrderIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> ids = List.of(10001L, 10002L, 10003L);

            // when
            BooleanExpression result = conditionBuilder.legacyOrderIdIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void legacyOrderIdIn_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.legacyOrderIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void legacyOrderIdIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.legacyOrderIdIn(List.of());

            // then
            assertThat(result).isNull();
        }
    }
}
