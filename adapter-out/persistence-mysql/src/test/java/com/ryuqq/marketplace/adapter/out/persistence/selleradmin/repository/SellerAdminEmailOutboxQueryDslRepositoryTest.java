package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminEmailOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminEmailOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
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

/** SellerAdminEmailOutboxQueryDslRepositoryTest - 셀러 관리자 이메일 Outbox QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerAdminEmailOutboxQueryDslRepository 통합 테스트")
class SellerAdminEmailOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerAdminEmailOutboxQueryDslRepository repository() {
        return new SellerAdminEmailOutboxQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerAdminEmailOutboxConditionBuilder());
    }

    private SellerAdminEmailOutboxJpaEntity persist(SellerAdminEmailOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findPendingBySellerId")
    class FindPendingBySellerIdTest {

        @Test
        @DisplayName("PENDING Outbox는 sellerId로 조회됩니다")
        void findPendingBySellerId_WithPendingEntity_ReturnsEntity() {
            // given
            long sellerId = 10L;
            persist(SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntityWithSellerId(sellerId));

            // when
            Optional<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findPendingBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
            assertThat(result.get().getStatus())
                    .isEqualTo(SellerAdminEmailOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("PROCESSING 상태는 조회되지 않습니다")
        void findPendingBySellerId_WithProcessingEntity_ReturnsEmpty() {
            // given
            long sellerId = 11L;
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntityWithSellerId(sellerId);
            entity =
                    SellerAdminEmailOutboxJpaEntity.create(
                            null,
                            sellerId,
                            entity.getPayload(),
                            SellerAdminEmailOutboxJpaEntity.Status.PROCESSING,
                            entity.getRetryCount(),
                            entity.getMaxRetry(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt(),
                            null,
                            null,
                            entity.getVersion(),
                            entity.getIdempotencyKey());
            persist(entity);

            // when
            Optional<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findPendingBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 sellerId는 빈 결과를 반환합니다")
        void findPendingBySellerId_WithNonExistingSellerId_ReturnsEmpty() {
            // given
            long sellerId = 999L;

            // when
            Optional<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findPendingBySellerId(sellerId);

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

            // 60초 전 생성 (조회 대상)
            persist(
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            10L, beforeTime.minusSeconds(10)));
            // 30초 전 생성 (조회 제외)
            persist(
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            11L, beforeTime.plusSeconds(30)));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSellerId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("최대 재시도에 도달한 Outbox는 제외됩니다")
        void findPendingOutboxesForRetry_WithMaxRetry_Excludes() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);

            // 재시도 가능 (retryCount < maxRetry)
            persist(SellerAdminEmailOutboxJpaEntityFixtures.retriedPendingEntity(1));
            // 최대 재시도 도달 (retryCount >= maxRetry)
            persist(SellerAdminEmailOutboxJpaEntityFixtures.pendingEntityMaxRetry());

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
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
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            20L, now.minusSeconds(300)));
            persist(
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            21L, now.minusSeconds(200)));
            persist(
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntityWithCreatedAt(
                            22L, now.minusSeconds(100)));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findPendingOutboxesForRetry(now, 10);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSellerId()).isEqualTo(20L);
            assertThat(result.get(1).getSellerId()).isEqualTo(21L);
            assertThat(result.get(2).getSellerId()).isEqualTo(22L);
        }

        @Test
        @DisplayName("limit 제한을 준수합니다")
        void findPendingOutboxesForRetry_RespectsLimit() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);

            persist(SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntityWithSellerId(30L));
            persist(SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntityWithSellerId(31L));
            persist(SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntityWithSellerId(32L));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
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

            // 타임아웃 (400초 전)
            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(400));
            // 타임아웃 아님 (100초 전)
            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(100));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(SellerAdminEmailOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("PENDING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithPendingStatus_Excludes() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            persist(SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntityWithSellerId(40L));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("updatedAt 오름차순으로 정렬됩니다")
        void findProcessingTimeoutOutboxes_OrdersByUpdatedAtAscending() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(600));
            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(500));
            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(400));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(3);
            // 오래된 것부터 (600 → 500 → 400)
            assertThat(result.get(0).getUpdatedAt()).isBefore(result.get(1).getUpdatedAt());
            assertThat(result.get(1).getUpdatedAt()).isBefore(result.get(2).getUpdatedAt());
        }

        @Test
        @DisplayName("limit 제한을 준수합니다")
        void findProcessingTimeoutOutboxes_RespectsLimit() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(600));
            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(500));
            persist(SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(400));

            // when
            List<SellerAdminEmailOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
