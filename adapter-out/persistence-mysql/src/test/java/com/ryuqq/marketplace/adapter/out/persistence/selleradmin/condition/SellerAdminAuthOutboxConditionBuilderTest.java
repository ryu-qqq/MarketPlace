package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAdminAuthOutboxConditionBuilderTest - 셀러 관리자 인증 Outbox 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerAdminAuthOutboxConditionBuilder 단위 테스트")
class SellerAdminAuthOutboxConditionBuilderTest {

    private SellerAdminAuthOutboxConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerAdminAuthOutboxConditionBuilder();
    }

    // ========================================================================
    // 1. sellerAdminIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerAdminIdEq 메서드 테스트")
    class SellerAdminIdEqTest {

        @Test
        @DisplayName("유효한 셀러 관리자 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerAdminIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            String sellerAdminId = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

            // when
            BooleanExpression result = conditionBuilder.sellerAdminIdEq(sellerAdminId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 관리자 ID 입력 시 null을 반환합니다")
        void sellerAdminIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerAdminIdEq(null);

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
            SellerAdminAuthOutboxJpaEntity.Status status =
                    SellerAdminAuthOutboxJpaEntity.Status.PENDING;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PROCESSING 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithProcessingStatus_ReturnsBooleanExpression() {
            // given
            SellerAdminAuthOutboxJpaEntity.Status status =
                    SellerAdminAuthOutboxJpaEntity.Status.PROCESSING;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("COMPLETED 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithCompletedStatus_ReturnsBooleanExpression() {
            // given
            SellerAdminAuthOutboxJpaEntity.Status status =
                    SellerAdminAuthOutboxJpaEntity.Status.COMPLETED;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithFailedStatus_ReturnsBooleanExpression() {
            // given
            SellerAdminAuthOutboxJpaEntity.Status status =
                    SellerAdminAuthOutboxJpaEntity.Status.FAILED;

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
        @DisplayName("재시도 가능 조건 BooleanExpression을 반환합니다")
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
    // 7. updatedAtBefore 테스트
    // ========================================================================

    @Nested
    @DisplayName("updatedAtBefore 메서드 테스트")
    class UpdatedAtBeforeTest {

        @Test
        @DisplayName("유효한 시간 입력 시 BooleanExpression을 반환합니다")
        void updatedAtBefore_WithValidTime_ReturnsBooleanExpression() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);

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
