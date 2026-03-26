package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition.LegacyOrderConversionOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * LegacyOrderConversionOutboxQueryDslRepositoryTest - 레거시 주문 변환 Outbox QueryDslRepository 통합 테스트.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("LegacyOrderConversionOutboxQueryDslRepository 통합 테스트")
class LegacyOrderConversionOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyOrderConversionOutboxQueryDslRepository repository() {
        return new LegacyOrderConversionOutboxQueryDslRepository(
                new JPAQueryFactory(entityManager),
                new LegacyOrderConversionOutboxConditionBuilder());
    }

    private LegacyOrderConversionOutboxJpaEntity persist(
            LegacyOrderConversionOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findPendingOutboxes")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태이고 retryCount < maxRetry인 Outbox를 조회합니다")
        void findPendingOutboxes_WithValidConditions_ReturnsOutboxes() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
            assertThat(result).allMatch(e -> "PENDING".equals(e.getStatus()));
        }

        @Test
        @DisplayName("최대 재시도 횟수에 도달한 Outbox는 제외됩니다")
        void findPendingOutboxes_WithMaxRetryReached_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(
                    LegacyOrderConversionOutboxJpaEntityFixtures.retriedPendingEntity(
                            3)); // max_retry = 3

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).allMatch(e -> e.getRetryCount() < e.getMaxRetry());
        }

        @Test
        @DisplayName("PROCESSING 상태는 제외됩니다")
        void findPendingOutboxes_WithProcessingStatus_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).noneMatch(e -> "PROCESSING".equals(e.getStatus()));
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findPendingOutboxes_WithLimit_ReturnsLimitedResults() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
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
            persist(
                    LegacyOrderConversionOutboxJpaEntityFixtures.processingTimeoutEntity(
                            300)); // 5분 전

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result).allMatch(e -> "PROCESSING".equals(e.getStatus()));
        }

        @Test
        @DisplayName("최신 PROCESSING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithRecentProcessing_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(60);
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithPendingStatus_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findProcessingTimeoutOutboxes_WithLimit_ReturnsLimitedResults() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.processingTimeoutEntity(300));
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.processingTimeoutEntity(400));
            persist(LegacyOrderConversionOutboxJpaEntityFixtures.processingTimeoutEntity(500));

            // when
            List<LegacyOrderConversionOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("existsByLegacyOrderId")
    class ExistsByLegacyOrderIdTest {

        @Test
        @DisplayName("해당 legacyOrderId의 Outbox가 존재하면 true를 반환합니다")
        void existsByLegacyOrderId_WithExistingOutbox_ReturnsTrue() {
            // given
            long legacyOrderId = 99999L;
            persist(
                    LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntityWithOrderId(
                            legacyOrderId));

            // when
            boolean result = repository().existsByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("해당 legacyOrderId의 Outbox가 없으면 false를 반환합니다")
        void existsByLegacyOrderId_WithNoOutbox_ReturnsFalse() {
            // when
            boolean result = repository().existsByLegacyOrderId(88888L);

            // then
            assertThat(result).isFalse();
        }
    }
}
