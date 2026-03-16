package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OutboundSyncOutboxConditionBuilder 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OutboundSyncOutboxConditionBuilder 단위 테스트")
class OutboundSyncOutboxConditionBuilderTest {

    private OutboundSyncOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new OutboundSyncOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. productGroupIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("productGroupIdEq 메서드 테스트")
    class ProductGroupIdEqTest {

        @Test
        @DisplayName("유효한 상품그룹 ID 입력 시 BooleanExpression을 반환합니다")
        void productGroupIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long productGroupId = 100L;

            // when
            BooleanExpression result = conditionBuilder.productGroupIdEq(productGroupId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 상품그룹 ID 입력 시 null을 반환합니다")
        void productGroupIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. statusPending 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusPending 메서드 테스트")
    class StatusPendingTest {

        @Test
        @DisplayName("항상 PENDING 상태 BooleanExpression을 반환합니다")
        void statusPending_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusPending();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. statusProcessing 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusProcessing 메서드 테스트")
    class StatusProcessingTest {

        @Test
        @DisplayName("항상 PROCESSING 상태 BooleanExpression을 반환합니다")
        void statusProcessing_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusProcessing();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4-1. statusPendingOrProcessingOrFailed 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusPendingOrProcessingOrFailed 메서드 테스트")
    class StatusPendingOrProcessingOrFailedTest {

        @Test
        @DisplayName("항상 PENDING/PROCESSING/FAILED 상태 BooleanExpression을 반환합니다")
        void statusPendingOrProcessingOrFailed_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusPendingOrProcessingOrFailed();

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("PENDING", "PROCESSING", "FAILED");
        }
    }

    // ========================================================================
    // 5. retryCountLtMaxRetry 테스트
    // ========================================================================

    @Nested
    @DisplayName("retryCountLtMaxRetry 메서드 테스트")
    class RetryCountLtMaxRetryTest {

        @Test
        @DisplayName("항상 재시도 횟수 < 최대 재시도 BooleanExpression을 반환합니다")
        void retryCountLtMaxRetry_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.retryCountLtMaxRetry();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 6. createdAtBefore 테스트
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
        @DisplayName("null 시간 입력 시 null을 반환합니다")
        void createdAtBefore_WithNullTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.createdAtBefore(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. updatedAtBefore 테스트
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
        @DisplayName("null 시간 입력 시 null을 반환합니다")
        void updatedAtBefore_WithNullTime_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.updatedAtBefore(null);

            // then
            assertThat(result).isNull();
        }
    }
}
