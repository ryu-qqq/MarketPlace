package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerAuthOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** SellerAuthOutboxQueryDslRepositoryTest - 셀러 인증 Outbox QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerAuthOutboxQueryDslRepository 통합 테스트")
class SellerAuthOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerAuthOutboxQueryDslRepository repository() {
        return new SellerAuthOutboxQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerAuthOutboxConditionBuilder());
    }

    private SellerAuthOutboxJpaEntity persist(SellerAuthOutboxJpaEntity entity) {
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
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(sellerId));

            // when
            var result = repository().findPendingBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
            assertThat(result.get().getStatus())
                    .isEqualTo(SellerAuthOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("PROCESSING 상태는 조회되지 않습니다")
        void findPendingBySellerId_WithProcessingEntity_ReturnsEmpty() {
            // given
            long sellerId = 20L;
            persist(SellerAuthOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            var result = repository().findPendingBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태는 조회되지 않습니다")
        void findPendingBySellerId_WithCompletedEntity_ReturnsEmpty() {
            // given
            long sellerId = 30L;
            persist(SellerAuthOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            var result = repository().findPendingBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findPendingOutboxesForRetry")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("재시도 대상 PENDING Outbox 목록을 조회합니다")
        void findPendingOutboxesForRetry_WithValidConditions_ReturnsOutboxes() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(100L));
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(101L));

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(e -> e.getStatus() == SellerAuthOutboxJpaEntity.Status.PENDING);
            assertThat(result).allMatch(e -> e.getRetryCount() < e.getMaxRetry());
        }

        @Test
        @DisplayName("최대 재시도 횟수 도달한 Outbox는 제외됩니다")
        void findPendingOutboxesForRetry_WithMaxRetryReached_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(200L));
            persist(SellerAuthOutboxJpaEntityFixtures.retriedPendingEntity(3)); // max_retry = 3

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSellerId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("PROCESSING 상태는 제외됩니다")
        void findPendingOutboxesForRetry_WithProcessingStatus_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(300L));
            persist(SellerAuthOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSellerId()).isEqualTo(300L);
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findPendingOutboxesForRetry_WithLimit_ReturnsLimitedResults() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(400L));
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(401L));
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(402L));

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 2);

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
            persist(SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(300)); // 5분 전

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(SellerAuthOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("최신 PROCESSING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithRecentProcessing_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(60);
            persist(SellerAuthOutboxJpaEntityFixtures.newProcessingEntity()); // 방금 생성

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithPendingStatus_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(500L));

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findProcessingTimeoutOutboxes_WithLimit_ReturnsLimitedResults() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(300));
            persist(SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(400));
            persist(SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(500));

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
