package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.CancelOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxStatus;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CancelOutboxJpaEntityMapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만 포함.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("CancelOutboxJpaEntityMapper 단위 테스트")
class CancelOutboxJpaEntityMapperTest {

    private CancelOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CancelOutboxJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("신규 CancelOutbox (isNew=true)를 Entity로 변환 시 id가 null로 설정됩니다")
        void toEntity_WithNewCancelOutbox_SetsIdAsNull() {
            // given
            CancelOutbox domain = CancelFixtures.newCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("기존 CancelOutbox (isNew=false)를 Entity로 변환 시 id가 설정됩니다")
        void toEntity_WithReconstitutedCancelOutbox_SetsEntityId() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
        }

        @Test
        @DisplayName("orderItemId가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithOrderItemId_MapsOrderItemIdCorrectly() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
        }

        @Test
        @DisplayName("outboxType이 name()으로 Entity에 매핑됩니다")
        void toEntity_WithOutboxType_MapsTypeAsName() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOutboxType()).isEqualTo(CancelOutboxType.APPROVE.name());
        }

        @Test
        @DisplayName("status가 name()으로 Entity에 매핑됩니다")
        void toEntity_WithPendingStatus_MapsStatusAsName() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(CancelOutboxStatus.PENDING.name());
        }

        @Test
        @DisplayName("payload, retryCount, maxRetry가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithRetryFields_MapsRetryFieldsCorrectly() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
        }

        @Test
        @DisplayName("idempotencyKey가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithIdempotencyKey_MapsKeyCorrectly() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("version이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithVersion_MapsVersionCorrectly() {
            // given
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            // when
            CancelOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
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
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.outboxType())
                    .isEqualTo(CancelOutboxType.valueOf(entity.getOutboxType()));
            assertThat(domain.status()).isEqualTo(CancelOutboxStatus.valueOf(entity.getStatus()));
        }

        @Test
        @DisplayName("payload, retryCount, maxRetry가 Domain으로 올바르게 복원됩니다")
        void toDomain_WithRetryFields_RestoresRetryFieldsCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntityWithRetry(2);

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
        }

        @Test
        @DisplayName("idempotencyKey가 Domain으로 올바르게 복원됩니다")
        void toDomain_WithIdempotencyKey_RestoresKeyCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithProcessingEntity_ConvertsStatusCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.processingEntity();

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(CancelOutboxStatus.PROCESSING);
            assertThat(domain.isProcessing()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsStatusCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.completedEntity();

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(CancelOutboxStatus.COMPLETED);
            assertThat(domain.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsStatusAndErrorMessageCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.failedEntity();

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(CancelOutboxStatus.FAILED);
            assertThat(domain.isFailed()).isTrue();
            assertThat(domain.errorMessage()).isEqualTo(entity.getErrorMessage());
        }

        @Test
        @DisplayName("version이 Domain으로 올바르게 복원됩니다")
        void toDomain_WithVersion_RestoresVersionCorrectly() {
            // given
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.completedEntity();

            // when
            CancelOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.version()).isEqualTo(entity.getVersion());
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
            CancelOutboxJpaEntity original = CancelOutboxJpaEntityFixtures.pendingEntity();

            // when
            CancelOutbox domain = mapper.toDomain(original);
            CancelOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getOutboxType()).isEqualTo(original.getOutboxType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getPayload()).isEqualTo(original.getPayload());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
        }

        @Test
        @DisplayName("FAILED Entity 양방향 변환 시 errorMessage가 보존됩니다")
        void roundTrip_FailedEntity_PreservesErrorMessage() {
            // given
            CancelOutboxJpaEntity original = CancelOutboxJpaEntityFixtures.failedEntity();

            // when
            CancelOutbox domain = mapper.toDomain(original);
            CancelOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getErrorMessage()).isEqualTo(original.getErrorMessage());
        }
    }
}
