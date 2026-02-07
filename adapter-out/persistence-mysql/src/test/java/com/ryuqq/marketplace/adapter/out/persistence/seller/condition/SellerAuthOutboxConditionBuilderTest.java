package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAuthOutboxConditionBuilderTest - 셀러 인증 Outbox 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerAuthOutboxConditionBuilder 단위 테스트")
class SellerAuthOutboxConditionBuilderTest {

    private SellerAuthOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerAuthOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. statusEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusEq 메서드 테스트")
    class StatusEqTest {

        @Test
        @DisplayName("PENDING 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithPendingStatus_ReturnsBooleanExpression() {
            // given
            SellerAuthOutboxJpaEntity.Status status = SellerAuthOutboxJpaEntity.Status.PENDING;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PROCESSING 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithProcessingStatus_ReturnsBooleanExpression() {
            // given
            SellerAuthOutboxJpaEntity.Status status = SellerAuthOutboxJpaEntity.Status.PROCESSING;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 상태 입력 시 null을 반환합니다")
        void statusEq_WithNullStatus_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusEq(null);

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
