package com.ryuqq.marketplace.integration.outboundsync;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * OutboundSyncOutbox JPA Repository 통합 테스트.
 *
 * <p>OutboundSyncOutboxJpaRepository의 기본 CRUD 동작을 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>save - 단건 저장 및 ID 채번
 *   <li>saveAll - 다건 저장
 *   <li>findById - ID로 단건 조회
 *   <li>기본 JPA 동작 검증
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("outboundsync")
@DisplayName("OutboundSyncOutbox Repository 통합 테스트")
class OutboundSyncOutboxRepositoryE2ETest extends E2ETestBase {

    @Autowired private OutboundSyncOutboxJpaRepository outboundSyncOutboxRepository;

    @BeforeEach
    void setUp() {
        outboundSyncOutboxRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        outboundSyncOutboxRepository.deleteAll();
    }

    // ========================================================================
    // 1. save 기본 동작 테스트
    // ========================================================================

    @Nested
    @DisplayName("save 기본 동작 테스트")
    class SaveTest {

        @Test
        @Tag("P0")
        @DisplayName("[R1-S01] PENDING 상태 단건 저장 후 ID가 채번됩니다")
        void save_PendingEntity_AssignsId() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();

            // when
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(saved.getSyncType()).isEqualTo(OutboundSyncOutboxJpaEntity.SyncType.CREATE);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-S02] PROCESSING 상태 엔티티를 저장합니다")
        void save_ProcessingEntity_Persists() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newProcessingEntity();

            // when
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-S03] COMPLETED 상태 엔티티 저장 후 processedAt이 보존됩니다")
        void save_CompletedEntity_PreservesProcessedAt() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity();

            // when
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.COMPLETED);
            assertThat(saved.getProcessedAt()).isNotNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-S04] FAILED 상태 엔티티 저장 후 errorMessage가 보존됩니다")
        void save_FailedEntity_PreservesErrorMessage() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newFailedEntity();

            // when
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.FAILED);
            assertThat(saved.getErrorMessage()).isNotNull();
            assertThat(saved.getRetryCount()).isEqualTo(saved.getMaxRetry());
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-S05] 저장 후 findById로 조회 시 모든 필드가 일치합니다")
        void save_ThenFindById_AllFieldsMatch() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();

            // when
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);
            Optional<OutboundSyncOutboxJpaEntity> found =
                    outboundSyncOutboxRepository.findById(saved.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getProductGroupId()).isEqualTo(saved.getProductGroupId());
            assertThat(found.get().getSalesChannelId()).isEqualTo(saved.getSalesChannelId());
            assertThat(found.get().getSellerId()).isEqualTo(saved.getSellerId());
            assertThat(found.get().getSyncType()).isEqualTo(saved.getSyncType());
            assertThat(found.get().getStatus()).isEqualTo(saved.getStatus());
            assertThat(found.get().getPayload()).isEqualTo(saved.getPayload());
            assertThat(found.get().getRetryCount()).isEqualTo(saved.getRetryCount());
            assertThat(found.get().getMaxRetry()).isEqualTo(saved.getMaxRetry());
            assertThat(found.get().getIdempotencyKey()).isEqualTo(saved.getIdempotencyKey());
        }
    }

    // ========================================================================
    // 2. saveAll 기본 동작 테스트
    // ========================================================================

    @Nested
    @DisplayName("saveAll 기본 동작 테스트")
    class SaveAllTest {

        @Test
        @Tag("P0")
        @DisplayName("[R2-S01] 여러 건 저장 후 모두 조회됩니다")
        void saveAll_MultipleEntities_AllPersisted() {
            // given
            List<OutboundSyncOutboxJpaEntity> entities =
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<OutboundSyncOutboxJpaEntity> saved =
                    outboundSyncOutboxRepository.saveAll(entities);

            // then
            assertThat(saved).hasSize(3);
            assertThat(saved).allMatch(e -> e.getId() != null);
            assertThat(outboundSyncOutboxRepository.count()).isEqualTo(3);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R2-S02] 혼합 상태 엔티티들을 일괄 저장합니다")
        void saveAll_MixedStatusEntities_AllPersisted() {
            // given
            List<OutboundSyncOutboxJpaEntity> entities =
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newProcessingEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());

            // when
            List<OutboundSyncOutboxJpaEntity> saved =
                    outboundSyncOutboxRepository.saveAll(entities);

            // then
            assertThat(saved).hasSize(4);
            assertThat(saved).allMatch(e -> e.getId() != null);
            assertThat(outboundSyncOutboxRepository.count()).isEqualTo(4);
        }
    }

    // ========================================================================
    // 3. 상품그룹ID + 판매채널ID 조합 저장 테스트
    // ========================================================================

    @Nested
    @DisplayName("상품그룹ID + 판매채널ID 조합 저장 테스트")
    class ProductGroupAndSalesChannelTest {

        @Test
        @Tag("P0")
        @DisplayName("[R3-S01] 특정 상품그룹ID + 판매채널ID 조합으로 저장합니다")
        void save_WithSpecificProductGroupAndSalesChannel_Persists() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(200L, 20L);

            // when
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);

            // then
            Optional<OutboundSyncOutboxJpaEntity> found =
                    outboundSyncOutboxRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getProductGroupId()).isEqualTo(200L);
            assertThat(found.get().getSalesChannelId()).isEqualTo(20L);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R3-S02] 동일 상품그룹ID에 다른 판매채널ID 조합으로 여러 건 저장합니다")
        void saveAll_SameProductGroupDifferentSalesChannels_AllPersisted() {
            // given
            Long productGroupId = 300L;
            List<OutboundSyncOutboxJpaEntity> entities =
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    productGroupId, 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    productGroupId, 20L),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    productGroupId, 30L));

            // when
            List<OutboundSyncOutboxJpaEntity> saved =
                    outboundSyncOutboxRepository.saveAll(entities);

            // then
            assertThat(saved).hasSize(3);
            assertThat(saved).allMatch(e -> e.getProductGroupId().equals(productGroupId));
        }
    }

    // ========================================================================
    // 4. 존재하지 않는 ID 조회 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 기본 동작 테스트")
    class FindByIdTest {

        @Test
        @Tag("P0")
        @DisplayName("[R4-F01] 존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_NonExistingId_ReturnsEmpty() {
            // when
            Optional<OutboundSyncOutboxJpaEntity> result =
                    outboundSyncOutboxRepository.findById(99999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R4-S01] 저장된 ID로 조회 시 엔티티를 반환합니다")
        void findById_ExistingId_ReturnsEntity() {
            // given
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();
            OutboundSyncOutboxJpaEntity saved = outboundSyncOutboxRepository.save(entity);

            // when
            Optional<OutboundSyncOutboxJpaEntity> found =
                    outboundSyncOutboxRepository.findById(saved.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(saved.getId());
        }
    }
}
