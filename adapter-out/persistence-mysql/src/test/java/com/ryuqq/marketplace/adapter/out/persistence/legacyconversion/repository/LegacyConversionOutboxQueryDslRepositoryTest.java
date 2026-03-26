package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition.LegacyConversionOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** LegacyConversionOutboxQueryDslRepositoryTest - 레거시 변환 Outbox QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("LegacyConversionOutboxQueryDslRepository 통합 테스트")
class LegacyConversionOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyConversionOutboxQueryDslRepository repository() {
        return new LegacyConversionOutboxQueryDslRepository(
                new JPAQueryFactory(entityManager), new LegacyConversionOutboxConditionBuilder());
    }

    private LegacyConversionOutboxJpaEntity persist(LegacyConversionOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findPendingByLegacyProductGroupId")
    class FindPendingByLegacyProductGroupIdTest {

        @Test
        @DisplayName("PENDING 상태의 Outbox를 legacyProductGroupId로 조회합니다")
        void findPendingByLegacyProductGroupId_WithPendingEntity_ReturnsEntity() {
            // given
            long groupId = 9901L;
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntityWithGroupId(groupId));

            // when
            Optional<LegacyConversionOutboxJpaEntity> result =
                    repository().findPendingByLegacyProductGroupId(groupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getLegacyProductGroupId()).isEqualTo(groupId);
            assertThat(result.get().getStatus())
                    .isEqualTo(LegacyConversionOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("PROCESSING 상태는 조회되지 않습니다")
        void findPendingByLegacyProductGroupId_WithProcessingEntity_ReturnsEmpty() {
            // given
            long groupId = 9902L;
            persist(LegacyConversionOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            Optional<LegacyConversionOutboxJpaEntity> result =
                    repository().findPendingByLegacyProductGroupId(groupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태는 조회되지 않습니다")
        void findPendingByLegacyProductGroupId_WithCompletedEntity_ReturnsEmpty() {
            // given
            long groupId = 9903L;
            persist(LegacyConversionOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            Optional<LegacyConversionOutboxJpaEntity> result =
                    repository().findPendingByLegacyProductGroupId(groupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findPendingOutboxes")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태이고 retryCount < maxRetry인 Outbox를 조회합니다")
        void findPendingOutboxes_WithValidConditions_ReturnsOutboxes() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<LegacyConversionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
            assertThat(result)
                    .allMatch(e -> e.getStatus() == LegacyConversionOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("최대 재시도 횟수에 도달한 Outbox는 제외됩니다")
        void findPendingOutboxes_WithMaxRetryReached_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(
                    LegacyConversionOutboxJpaEntityFixtures.retriedPendingEntity(
                            3)); // max_retry = 3

            // when
            List<LegacyConversionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).allMatch(e -> e.getRetryCount() < e.getMaxRetry());
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findPendingOutboxes_WithLimit_ReturnsLimitedResults() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<LegacyConversionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 조회합니다")
        void findProcessingTimeoutOutboxes_WithTimeoutEntities_ReturnsOutboxes() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(LegacyConversionOutboxJpaEntityFixtures.processingTimeoutEntity(300)); // 5분 전

            // when
            List<LegacyConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result)
                    .allMatch(
                            e ->
                                    e.getStatus()
                                            == LegacyConversionOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("최신 PROCESSING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithRecentProcessing_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(60);
            persist(LegacyConversionOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<LegacyConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithPendingStatus_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<LegacyConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findExistingLegacyProductGroupIds")
    class FindExistingLegacyProductGroupIdsTest {

        @Test
        @DisplayName("존재하는 ID들을 집합으로 반환합니다")
        void findExistingLegacyProductGroupIds_WithExistingIds_ReturnsMatchingSet() {
            // given
            long groupId1 = 8801L;
            long groupId2 = 8802L;
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntityWithGroupId(groupId1));
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntityWithGroupId(groupId2));

            // when
            Set<Long> result =
                    repository()
                            .findExistingLegacyProductGroupIds(List.of(groupId1, groupId2, 9999L));

            // then
            assertThat(result).contains(groupId1, groupId2);
            assertThat(result).doesNotContain(9999L);
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 집합을 반환합니다")
        void findExistingLegacyProductGroupIds_WithEmptyList_ReturnsEmptySet() {
            // when
            Set<Long> result = repository().findExistingLegacyProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countDistinctLegacyProductGroupIds")
    class CountDistinctLegacyProductGroupIdsTest {

        @Test
        @DisplayName("고유한 legacyProductGroupId 수를 반환합니다")
        void countDistinctLegacyProductGroupIds_ReturnsCorrectCount() {
            // given
            long groupId = 7701L;
            persist(LegacyConversionOutboxJpaEntityFixtures.newPendingEntityWithGroupId(groupId));
            persist(LegacyConversionOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            long count = repository().countDistinctLegacyProductGroupIds();

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }
}
