package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.RefundOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import com.ryuqq.marketplace.domain.refund.outbox.RefundOutboxFixtures;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxStatus;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefundOutboxJpaEntityMapperTest - 환불 아웃박스 Entity-Domain 매퍼 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("RefundOutboxJpaEntityMapper 단위 테스트")
class RefundOutboxJpaEntityMapperTest {

    private RefundOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RefundOutboxJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("신규 Domain(isNew=true)을 Entity로 변환 시 id가 null입니다")
        void toEntity_WithNewDomain_ReturnsEntityWithNullId() {
            // given
            RefundOutbox domain = RefundOutboxFixtures.newRefundOutbox();

            // when
            RefundOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("기존 Domain(isNew=false)을 Entity로 변환 시 id가 설정됩니다")
        void toEntity_WithExistingDomain_ReturnsEntityWithId() {
            // given
            RefundOutbox domain = RefundOutboxFixtures.pendingRefundOutbox();

            // when
            RefundOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
        }

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingDomain_ConvertsStatusCorrectly() {
            // given
            RefundOutbox domain = RefundOutboxFixtures.pendingRefundOutbox();

            // when
            RefundOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(RefundOutboxStatus.PENDING.name());
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
            assertThat(entity.getOutboxType()).isEqualTo(domain.outboxType().name());
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedDomain_ConvertsCorrectly() {
            // given
            RefundOutbox domain = RefundOutboxFixtures.completedRefundOutbox();

            // when
            RefundOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(RefundOutboxStatus.COMPLETED.name());
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환 시 errorMessage가 설정됩니다")
        void toEntity_WithFailedDomain_SetsErrorMessage() {
            // given
            RefundOutbox domain = RefundOutboxFixtures.failedRefundOutbox();

            // when
            RefundOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(RefundOutboxStatus.FAILED.name());
            assertThat(entity.getErrorMessage()).isNotNull();
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
        }

        @Test
        @DisplayName("payload, version 필드가 정확히 변환됩니다")
        void toEntity_WithDomain_ConvertsPayloadAndVersion() {
            // given
            RefundOutbox domain = RefundOutboxFixtures.completedRefundOutbox();

            // when
            RefundOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getVersion()).isEqualTo(domain.version());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.pendingEntity();

            // when
            RefundOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.outboxType())
                    .isEqualTo(RefundOutboxType.valueOf(entity.getOutboxType()));
            assertThat(domain.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
        }

        @Test
        @DisplayName("COMPLETED Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.completedEntity();

            // when
            RefundOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(RefundOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("FAILED Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.failedEntity();

            // when
            RefundOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(RefundOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isNotNull();
            assertThat(domain.isFailed()).isTrue();
        }

        @Test
        @DisplayName("idempotencyKey 필드가 정확히 변환됩니다")
        void toDomain_WithEntity_ConvertsIdempotencyKey() {
            // given
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.pendingEntity();

            // when
            RefundOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            RefundOutboxJpaEntity original = RefundOutboxJpaEntityFixtures.pendingEntity();

            // when
            RefundOutbox domain = mapper.toDomain(original);
            RefundOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getOutboxType()).isEqualTo(original.getOutboxType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }
    }
}
