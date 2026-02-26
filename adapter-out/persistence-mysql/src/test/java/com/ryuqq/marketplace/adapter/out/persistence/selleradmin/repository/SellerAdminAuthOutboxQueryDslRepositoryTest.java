package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminAuthOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** SellerAdminAuthOutboxQueryDslRepositoryTest - 셀러 관리자 인증 Outbox QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerAdminAuthOutboxQueryDslRepository 통합 테스트")
class SellerAdminAuthOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerAdminAuthOutboxQueryDslRepository repository() {
        return new SellerAdminAuthOutboxQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerAdminAuthOutboxConditionBuilder());
    }

    private SellerAdminAuthOutboxJpaEntity persist(SellerAdminAuthOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findPendingBySellerAdminId")
    class FindPendingBySellerAdminIdTest {

        @Test
        @DisplayName("PENDING 상태의 Outbox는 sellerAdminId로 조회됩니다")
        void findPendingBySellerAdminId_WithPendingEntity_ReturnsEntity() {
            // given
            String sellerAdminId = "sa-pending-001";
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerAdminId(
                            sellerAdminId));

            // when
            Optional<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingBySellerAdminId(sellerAdminId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getSellerAdminId()).isEqualTo(sellerAdminId);
            assertThat(result.get().getStatus())
                    .isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("PROCESSING 상태는 조회되지 않습니다")
        void findPendingBySellerAdminId_WithProcessingEntity_ReturnsEmpty() {
            // given
            String sellerAdminId = "sa-processing-001";
            Instant now = Instant.now();
            SellerAdminAuthOutboxJpaEntity processingEntity =
                    SellerAdminAuthOutboxJpaEntity.create(
                            null,
                            sellerAdminId,
                            SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                            SellerAdminAuthOutboxJpaEntity.Status.PROCESSING,
                            0,
                            3,
                            now,
                            now,
                            null,
                            null,
                            0L,
                            "SAAO:" + sellerAdminId + ":" + now.toEpochMilli());
            persist(processingEntity);

            // when
            Optional<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingBySellerAdminId(sellerAdminId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태는 조회되지 않습니다")
        void findPendingBySellerAdminId_WithCompletedEntity_ReturnsEmpty() {
            // given
            String sellerAdminId = "sa-completed-001";
            Instant now = Instant.now();
            SellerAdminAuthOutboxJpaEntity completedEntity =
                    SellerAdminAuthOutboxJpaEntity.create(
                            null,
                            sellerAdminId,
                            SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                            SellerAdminAuthOutboxJpaEntity.Status.COMPLETED,
                            0,
                            3,
                            now,
                            now,
                            now,
                            null,
                            0L,
                            "SAAO:" + sellerAdminId + ":" + now.toEpochMilli());
            persist(completedEntity);

            // when
            Optional<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingBySellerAdminId(sellerAdminId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 sellerAdminId는 빈 결과를 반환합니다")
        void findPendingBySellerAdminId_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingBySellerAdminId("non-existing-sa-id");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findPendingOutboxesForRetry")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("beforeTime 이전에 생성된 PENDING Outbox를 조회합니다")
        void findPendingOutboxesForRetry_WithBeforeTime_ReturnsMatchingOutboxes() {
            // given
            Instant now = Instant.now();
            Instant beforeTime = now.minusSeconds(60);

            // 70초 전 생성 (조회 대상)
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            "sa-retry-target-001", beforeTime.minusSeconds(10)));
            // 30초 전 생성 (조회 제외 - beforeTime 이후)
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            "sa-retry-exclude-001", beforeTime.plusSeconds(30)));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSellerAdminId()).isEqualTo("sa-retry-target-001");
        }

        @Test
        @DisplayName("최대 재시도에 도달한 Outbox는 제외됩니다")
        void findPendingOutboxesForRetry_WithMaxRetry_Excludes() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);

            // 재시도 가능 (retryCount < maxRetry)
            persist(SellerAdminAuthOutboxJpaEntityFixtures.retriedPendingEntity(1));
            // 최대 재시도 도달 (retryCount >= maxRetry)
            persist(SellerAdminAuthOutboxJpaEntityFixtures.pendingEntityMaxRetry());

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRetryCount()).isLessThan(result.get(0).getMaxRetry());
        }

        @Test
        @DisplayName("createdAt 오름차순으로 정렬됩니다")
        void findPendingOutboxesForRetry_OrdersByCreatedAtAscending() {
            // given
            Instant now = Instant.now();

            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            "sa-sort-300", now.minusSeconds(300)));
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            "sa-sort-200", now.minusSeconds(200)));
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            "sa-sort-100", now.minusSeconds(100)));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingOutboxesForRetry(now, 10);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSellerAdminId()).isEqualTo("sa-sort-300");
            assertThat(result.get(1).getSellerAdminId()).isEqualTo("sa-sort-200");
            assertThat(result.get(2).getSellerAdminId()).isEqualTo("sa-sort-100");
        }

        @Test
        @DisplayName("limit 제한을 준수합니다")
        void findPendingOutboxesForRetry_RespectsLimit() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);

            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerAdminId(
                            "sa-limit-001"));
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerAdminId(
                            "sa-limit-002"));
            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerAdminId(
                            "sa-limit-003"));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findPendingOutboxesForRetry(beforeTime, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("timeoutThreshold 이전에 업데이트된 PROCESSING Outbox를 조회합니다")
        void findProcessingTimeoutOutboxes_WithBeforeThreshold_ReturnsTimedOut() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // 타임아웃 (400초 전 업데이트)
            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(400));
            // 타임아웃 아님 (100초 전 업데이트)
            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(100));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("PENDING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithPendingStatus_Excludes() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            persist(
                    SellerAdminAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerAdminId(
                            "sa-timeout-pending-001"));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("updatedAt 오름차순으로 정렬됩니다")
        void findProcessingTimeoutOutboxes_OrdersByUpdatedAtAscending() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(600));
            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(500));
            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(400));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(3);
            // 오래된 것부터 (600 -> 500 -> 400)
            assertThat(result.get(0).getUpdatedAt()).isBefore(result.get(1).getUpdatedAt());
            assertThat(result.get(1).getUpdatedAt()).isBefore(result.get(2).getUpdatedAt());
        }

        @Test
        @DisplayName("limit 제한을 준수합니다")
        void findProcessingTimeoutOutboxes_RespectsLimit() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(600));
            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(500));
            persist(SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(400));

            // when
            List<SellerAdminAuthOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
