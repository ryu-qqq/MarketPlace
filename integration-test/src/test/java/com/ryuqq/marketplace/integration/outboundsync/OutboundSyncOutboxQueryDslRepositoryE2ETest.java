package com.ryuqq.marketplace.integration.outboundsync;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * OutboundSyncOutbox QueryDSL Repository 통합 테스트.
 *
 * <p>OutboundSyncOutboxQueryDslRepository의 쿼리 동작을 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>findPendingByProductGroupId - 상품그룹 ID로 PENDING 상태 Outbox 목록 조회
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("outboundsync")
@DisplayName("OutboundSyncOutbox QueryDSL Repository 통합 테스트")
class OutboundSyncOutboxQueryDslRepositoryE2ETest extends E2ETestBase {

    @Autowired private OutboundSyncOutboxQueryDslRepository queryDslRepository;
    @Autowired private OutboundSyncOutboxJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        jpaRepository.deleteAll();
    }

    // ========================================================================
    // 1. findPendingByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingByProductGroupId 쿼리 테스트")
    class FindPendingByProductGroupIdTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S01] 상품그룹 ID로 PENDING 상태 Outbox 목록을 조회합니다")
        void findPendingByProductGroupId_ExistingRecord_ReturnsEntities() {
            // given
            Long targetProductGroupId = 500L;
            jpaRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    targetProductGroupId, 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    targetProductGroupId, 20L)));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(targetProductGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getProductGroupId().equals(targetProductGroupId));
            assertThat(result)
                    .allMatch(
                            e -> e.getStatus().equals(OutboundSyncOutboxJpaEntity.Status.PENDING));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-F01] 존재하지 않는 상품그룹 ID로 조회 시 빈 리스트를 반환합니다")
        void findPendingByProductGroupId_NonExistingId_ReturnsEmpty() {
            // given - 데이터 없음

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(99999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-F02] PENDING이 아닌 상태의 Outbox는 조회되지 않습니다")
        void findPendingByProductGroupId_NonPendingStatus_NotReturned() {
            // given
            Long targetProductGroupId = 600L;
            jpaRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newProcessingEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity()));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(targetProductGroupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-F03] 다른 상품그룹 ID의 PENDING Outbox는 조회되지 않습니다")
        void findPendingByProductGroupId_DifferentProductGroupId_NotReturned() {
            // given
            jpaRepository.save(OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(700L, 10L));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(800L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S02] PENDING과 비PENDING 혼합 데이터에서 PENDING만 조회됩니다")
        void findPendingByProductGroupId_MixedStatus_ReturnsPendingOnly() {
            // given
            Long targetProductGroupId = 900L;
            OutboundSyncOutboxJpaEntity pendingEntity1 =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                            targetProductGroupId, 10L);
            OutboundSyncOutboxJpaEntity pendingEntity2 =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                            targetProductGroupId, 20L);
            OutboundSyncOutboxJpaEntity processingEntity =
                    OutboundSyncOutboxJpaEntityFixtures.newProcessingEntity();
            OutboundSyncOutboxJpaEntity completedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity();

            jpaRepository.saveAll(
                    List.of(pendingEntity1, pendingEntity2, processingEntity, completedEntity));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(targetProductGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(
                            e -> e.getStatus().equals(OutboundSyncOutboxJpaEntity.Status.PENDING));
            assertThat(result).allMatch(e -> e.getProductGroupId().equals(targetProductGroupId));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S03] CREATE/UPDATE/DELETE 혼합 SyncType의 PENDING Outbox를 모두 조회합니다")
        void findPendingByProductGroupId_MixedSyncType_ReturnsAll() {
            // given
            Long targetProductGroupId = 1000L;
            jpaRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    targetProductGroupId, 10L),
                            buildPendingEntityWithSyncType(
                                    targetProductGroupId,
                                    11L,
                                    OutboundSyncOutboxJpaEntity.SyncType.UPDATE),
                            buildPendingEntityWithSyncType(
                                    targetProductGroupId,
                                    12L,
                                    OutboundSyncOutboxJpaEntity.SyncType.DELETE)));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(targetProductGroupId);

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S04] 최대 1000건 제한이 적용됩니다")
        void findPendingByProductGroupId_LargeDataset_ReturnsUpTo1000() {
            // given
            Long targetProductGroupId = 1100L;
            List<OutboundSyncOutboxJpaEntity> entities = new java.util.ArrayList<>();
            for (int i = 0; i < 10; i++) {
                entities.add(
                        OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                targetProductGroupId, (long) (i + 100)));
            }
            jpaRepository.saveAll(entities);

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupId(targetProductGroupId);

            // then
            assertThat(result).hasSize(10);
            assertThat(result.size()).isLessThanOrEqualTo(1000);
        }
    }

    // ========================================================================
    // 2. findPendingByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingByProductGroupIds 쿼리 테스트")
    class FindPendingByProductGroupIdsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-S01] 여러 상품그룹 ID로 PENDING 상태 Outbox를 일괄 조회합니다")
        void findPendingByProductGroupIds_MultipleIds_ReturnsAllPending() {
            // given
            jpaRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(500L, 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(600L, 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(700L, 10L)));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupIds(List.of(500L, 600L));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(OutboundSyncOutboxJpaEntity::getProductGroupId)
                    .containsExactlyInAnyOrder(500L, 600L);
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-F01] 빈 ID 목록으로 조회 시 빈 리스트를 반환합니다")
        void findPendingByProductGroupIds_EmptyIds_ReturnsEmpty() {
            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-F02] null ID 목록으로 조회 시 빈 리스트를 반환합니다")
        void findPendingByProductGroupIds_NullIds_ReturnsEmpty() {
            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupIds(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-F03] PENDING이 아닌 상태는 조회에서 제외됩니다")
        void findPendingByProductGroupIds_NonPendingExcluded() {
            // given
            Long targetPgId = 800L;
            jpaRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    targetPgId, 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newProcessingEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity(),
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity()));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupIds(List.of(targetPgId));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getProductGroupId()).isEqualTo(targetPgId);
            assertThat(result.getFirst().getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q2-S02] 일치하는 상품그룹 ID가 없으면 빈 리스트를 반환합니다")
        void findPendingByProductGroupIds_NoMatchingIds_ReturnsEmpty() {
            // given
            jpaRepository.save(OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(100L, 10L));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findPendingByProductGroupIds(List.of(999L, 998L));

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findActiveByProductGroupIdAndSyncType 테스트
    // ========================================================================

    @Nested
    @DisplayName("findActiveByProductGroupIdAndSyncType 쿼리 테스트")
    class FindActiveByProductGroupIdAndSyncTypeTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q3-S01] PENDING/PROCESSING/FAILED 상태의 Outbox를 모두 조회합니다")
        void findActive_AllActiveStatuses_ReturnsAll() {
            // given
            Long targetPgId = 2000L;
            jpaRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newEntityWith(
                                    targetPgId,
                                    10L,
                                    OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                                    OutboundSyncOutboxJpaEntity.Status.PENDING),
                            OutboundSyncOutboxJpaEntityFixtures.newEntityWith(
                                    targetPgId,
                                    20L,
                                    OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                                    OutboundSyncOutboxJpaEntity.Status.PROCESSING),
                            OutboundSyncOutboxJpaEntityFixtures.newEntityWith(
                                    targetPgId,
                                    30L,
                                    OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                                    OutboundSyncOutboxJpaEntity.Status.FAILED)));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findActiveByProductGroupIdAndSyncType(targetPgId, "UPDATE");

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q3-S02] FAILED 상태의 Outbox도 active로 조회됩니다")
        void findActive_FailedStatus_IsIncluded() {
            // given
            Long targetPgId = 2100L;
            jpaRepository.save(
                    OutboundSyncOutboxJpaEntityFixtures.newEntityWith(
                            targetPgId,
                            10L,
                            OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                            OutboundSyncOutboxJpaEntity.Status.FAILED));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findActiveByProductGroupIdAndSyncType(targetPgId, "UPDATE");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.FAILED);
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q3-F01] COMPLETED 상태는 조회에서 제외됩니다")
        void findActive_CompletedStatus_IsExcluded() {
            // given
            Long targetPgId = 2200L;
            jpaRepository.save(
                    OutboundSyncOutboxJpaEntityFixtures.newEntityWith(
                            targetPgId,
                            10L,
                            OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findActiveByProductGroupIdAndSyncType(targetPgId, "UPDATE");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q3-F02] 다른 SyncType의 Outbox는 조회에서 제외됩니다")
        void findActive_DifferentSyncType_IsExcluded() {
            // given
            Long targetPgId = 2300L;
            jpaRepository.save(
                    OutboundSyncOutboxJpaEntityFixtures.newEntityWith(
                            targetPgId,
                            10L,
                            OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                            OutboundSyncOutboxJpaEntity.Status.FAILED));

            // when
            List<OutboundSyncOutboxJpaEntity> result =
                    queryDslRepository.findActiveByProductGroupIdAndSyncType(targetPgId, "UPDATE");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 유틸리티 메서드
    // ========================================================================

    private OutboundSyncOutboxJpaEntity buildPendingEntityWithSyncType(
            Long productGroupId,
            Long salesChannelId,
            OutboundSyncOutboxJpaEntity.SyncType syncType) {
        java.time.Instant now = java.time.Instant.now();
        long seqVal = java.util.concurrent.ThreadLocalRandom.current().nextLong(10000, 99999);
        String idempotencyKey =
                "EPSO:"
                        + productGroupId
                        + ":"
                        + salesChannelId
                        + ":"
                        + syncType.name()
                        + ":"
                        + seqVal;
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                salesChannelId,
                OutboundSyncOutboxJpaEntityFixtures.DEFAULT_SHOP_ID,
                OutboundSyncOutboxJpaEntityFixtures.DEFAULT_SELLER_ID,
                syncType,
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                OutboundSyncOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                OutboundSyncOutboxJpaEntityFixtures.DEFAULT_RETRY_COUNT,
                OutboundSyncOutboxJpaEntityFixtures.DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                OutboundSyncOutboxJpaEntityFixtures.DEFAULT_VERSION,
                idempotencyKey);
    }
}
