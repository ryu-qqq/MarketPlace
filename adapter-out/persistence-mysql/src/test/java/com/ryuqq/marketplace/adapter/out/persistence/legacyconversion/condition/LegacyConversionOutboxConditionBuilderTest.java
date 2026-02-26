package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyConversionOutboxConditionBuilderTest - Outbox 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyConversionOutboxConditionBuilder 단위 테스트")
class LegacyConversionOutboxConditionBuilderTest {

    private LegacyConversionOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new LegacyConversionOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. legacyProductGroupIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("legacyProductGroupIdEq 메서드 테스트")
    class LegacyProductGroupIdEqTest {

        @Test
        @DisplayName("유효한 legacyProductGroupId 입력 시 BooleanExpression을 반환합니다")
        void legacyProductGroupIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long legacyProductGroupId = 100L;

            // when
            BooleanExpression result =
                    conditionBuilder.legacyProductGroupIdEq(legacyProductGroupId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void legacyProductGroupIdEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.legacyProductGroupIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. statusPending 테스트
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
    // 3. statusProcessing 테스트
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
    // 4. retryCountLtMaxRetry 테스트
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
    // 5. createdAtBefore 테스트
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
    // 6. updatedAtBefore 테스트
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
}
