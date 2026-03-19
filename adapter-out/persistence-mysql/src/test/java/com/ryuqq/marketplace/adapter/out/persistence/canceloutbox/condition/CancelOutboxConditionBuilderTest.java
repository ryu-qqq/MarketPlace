package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CancelOutboxConditionBuilder 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("CancelOutboxConditionBuilder 단위 테스트")
class CancelOutboxConditionBuilderTest {

    private CancelOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CancelOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. statusPending 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusPending 메서드 테스트")
    class StatusPendingTest {

        @Test
        @DisplayName("PENDING 상태 조건 BooleanExpression을 반환합니다")
        void statusPending_Always_ReturnsBooleanExpression() {
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
        void statusProcessing_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusProcessing();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 3. createdAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtBefore 메서드 테스트")
    class CreatedAtBeforeTest {

        @Test
        @DisplayName("유효한 beforeTime 입력 시 BooleanExpression을 반환합니다")
        void createdAtBefore_WithValidBeforeTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null beforeTime 입력 시 null을 반환합니다")
        void createdAtBefore_WithNullBeforeTime_ReturnsNull() {
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
        @DisplayName("유효한 beforeTime 입력 시 BooleanExpression을 반환합니다")
        void updatedAtBefore_WithValidBeforeTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);

            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(beforeTime);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null beforeTime 입력 시 null을 반환합니다")
        void updatedAtBefore_WithNullBeforeTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("statusPending 과 updatedAtBefore 조건이 모두 not-null을 반환합니다")
        void updatedAtBefore_CombinedWithStatusProcessing_BothNotNull() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(180);

            // when
            BooleanExpression statusResult = conditionBuilder.statusProcessing();
            BooleanExpression timeResult = conditionBuilder.updatedAtBefore(timeoutBefore);

            // then
            assertThat(statusResult).isNotNull();
            assertThat(timeResult).isNotNull();
        }
    }
}
