package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OutboundSyncOutboxJpaEntityMapper 단위 테스트.
 *
 * <p>PER-MAP-001: Mapper는 Domain ↔ Entity 변환만 담당.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OutboundSyncOutboxJpaEntityMapper 테스트")
class OutboundSyncOutboxJpaEntityMapperTest {

    private final OutboundSyncOutboxJpaEntityMapper sut = new OutboundSyncOutboxJpaEntityMapper();

    // ========================================================================
    // 1. toEntity() 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity() - Domain → Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환한다")
        void toEntity_Pending() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.pendingOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelIdValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getSyncType()).isEqualTo(OutboundSyncOutboxJpaEntity.SyncType.CREATE);
            assertThat(entity.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
            assertThat(entity.getProcessedAt()).isNull();
            assertThat(entity.getErrorMessage()).isNull();
            assertThat(entity.getVersion()).isEqualTo(domain.version());
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("PROCESSING 상태 Domain을 Entity로 변환한다")
        void toEntity_Processing() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.processingOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환한다")
        void toEntity_Completed() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.completedOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환한다")
        void toEntity_Failed() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.failedOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("UPDATE SyncType Domain을 Entity로 변환한다")
        void toEntity_UpdateSyncType() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.newPendingUpdateOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getSyncType()).isEqualTo(OutboundSyncOutboxJpaEntity.SyncType.UPDATE);
        }

        @Test
        @DisplayName("DELETE SyncType Domain을 Entity로 변환한다")
        void toEntity_DeleteSyncType() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.newPendingDeleteOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getSyncType()).isEqualTo(OutboundSyncOutboxJpaEntity.SyncType.DELETE);
        }

        @Test
        @DisplayName("새 Domain(ID=null)을 Entity로 변환한다")
        void toEntity_NewDomain() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.newPendingOutbox();

            // when
            OutboundSyncOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
        }
    }

    // ========================================================================
    // 2. toDomain() 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain() - Entity → Domain 변환")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환한다")
        void toDomain_Pending() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.salesChannelIdValue()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.syncType()).isEqualTo(SyncType.CREATE);
            assertThat(domain.status()).isEqualTo(SyncStatus.PENDING);
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
            assertThat(domain.processedAt()).isNull();
            assertThat(domain.errorMessage()).isNull();
            assertThat(domain.version()).isEqualTo(entity.getVersion());
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity를 Domain으로 변환한다")
        void toDomain_Processing() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.processingEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SyncStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Completed() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.completedEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SyncStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Failed() {
            // given
            OutboundSyncOutboxJpaEntity entity = OutboundSyncOutboxJpaEntityFixtures.failedEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SyncStatus.FAILED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("UPDATE SyncType Entity를 Domain으로 변환한다")
        void toDomain_UpdateSyncType() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingUpdateEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.syncType()).isEqualTo(SyncType.UPDATE);
        }

        @Test
        @DisplayName("DELETE SyncType Entity를 Domain으로 변환한다")
        void toDomain_DeleteSyncType() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingDeleteEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.syncType()).isEqualTo(SyncType.DELETE);
        }

        @Test
        @DisplayName("ID가 null인 Entity를 toDomain() 호출 시 IllegalStateException을 던진다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();

            // when / then
            assertThatThrownBy(() -> sut.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id가 null");
        }
    }

    // ========================================================================
    // 3. 양방향 변환 일관성 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 일관성 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity → Domain → Entity 변환 시 값이 일치한다")
        void entityToDomainToEntity_ShouldPreserveValues() {
            // given
            OutboundSyncOutboxJpaEntity original =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(original);
            OutboundSyncOutboxJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getProductGroupId()).isEqualTo(original.getProductGroupId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getSyncType()).isEqualTo(original.getSyncType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getPayload()).isEqualTo(original.getPayload());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
            assertThat(converted.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(converted.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
            assertThat(converted.getProcessedAt()).isEqualTo(original.getProcessedAt());
            assertThat(converted.getErrorMessage()).isEqualTo(original.getErrorMessage());
            assertThat(converted.getVersion()).isEqualTo(original.getVersion());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }

        @Test
        @DisplayName("COMPLETED Entity → Domain → Entity 변환 시 processedAt이 보존된다")
        void entityToDomainToEntity_Completed_PreservesProcessedAt() {
            // given
            OutboundSyncOutboxJpaEntity original =
                    OutboundSyncOutboxJpaEntityFixtures.completedEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(original);
            OutboundSyncOutboxJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getProcessedAt()).isEqualTo(original.getProcessedAt());
            assertThat(converted.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.COMPLETED);
        }

        @Test
        @DisplayName("FAILED Entity → Domain → Entity 변환 시 errorMessage가 보존된다")
        void entityToDomainToEntity_Failed_PreservesErrorMessage() {
            // given
            OutboundSyncOutboxJpaEntity original =
                    OutboundSyncOutboxJpaEntityFixtures.failedEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(original);
            OutboundSyncOutboxJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getErrorMessage()).isEqualTo(original.getErrorMessage());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
        }
    }

    // ========================================================================
    // 4. retrySyncHistory 시나리오 - 상태 전이 후 변환 검증
    // ========================================================================

    @Nested
    @DisplayName("retrySyncHistory 시나리오 변환 테스트")
    class RetrySyncHistoryConversionTest {

        @Test
        @DisplayName("FAILED Entity → toDomain() → retry() → toEntity() 시 상태가 PENDING으로 변환됩니다")
        void failedEntityToDomain_AfterRetry_ConvertsToPendingEntity() {
            // given
            OutboundSyncOutboxJpaEntity failedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.failedEntity();

            // when: Entity → Domain 변환
            OutboundSyncOutbox domain = sut.toDomain(failedEntity);
            assertThat(domain.status()).isEqualTo(SyncStatus.FAILED);

            // when: Domain에서 retry() 호출
            domain.retry(Instant.now());
            assertThat(domain.status()).isEqualTo(SyncStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(0);
            assertThat(domain.errorMessage()).isNull();

            // when: Domain → Entity 변환
            OutboundSyncOutboxJpaEntity retryEntity = sut.toEntity(domain);

            // then
            assertThat(retryEntity.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(retryEntity.getRetryCount()).isEqualTo(0);
            assertThat(retryEntity.getErrorMessage()).isNull();
        }

        @Test
        @DisplayName("FAILED Entity toDomain() 후 retry() 시 retryCount가 0으로 초기화됩니다")
        void failedEntityToDomain_AfterRetry_ResetsRetryCount() {
            // given: maxRetry(3)만큼 소진된 FAILED entity
            OutboundSyncOutboxJpaEntity failedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.failedEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(failedEntity);
            int retryCountBeforeRetry = domain.retryCount();
            domain.retry(Instant.now());

            // then
            assertThat(retryCountBeforeRetry)
                    .isEqualTo(OutboundSyncOutboxFixtures.DEFAULT_MAX_RETRY);
            assertThat(domain.retryCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("retry() 후 toEntity() 변환 시 processedAt이 null로 초기화되지 않습니다")
        void failedEntityToDomain_AfterRetry_ProcessedAtPreservedInEntity() {
            // given
            OutboundSyncOutboxJpaEntity failedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.failedEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(failedEntity);
            domain.retry(Instant.now());
            OutboundSyncOutboxJpaEntity retryEntity = sut.toEntity(domain);

            // then: retry() 후 PENDING 전이 시 processedAt은 Domain에 보존됨 (null로 변경 안 됨)
            // OutboundSyncOutbox.retry()는 processedAt을 변경하지 않음
            assertThat(retryEntity.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("FAILED 상태가 아닌 Entity(PENDING)에서 retry() 호출 시 IllegalStateException을 던집니다")
        void pendingEntityToDomain_AfterRetry_ThrowsIllegalStateException() {
            // given
            OutboundSyncOutboxJpaEntity pendingEntity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();

            // when
            OutboundSyncOutbox domain = sut.toDomain(pendingEntity);

            // then
            assertThatThrownBy(() -> domain.retry(Instant.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다");
        }
    }
}
