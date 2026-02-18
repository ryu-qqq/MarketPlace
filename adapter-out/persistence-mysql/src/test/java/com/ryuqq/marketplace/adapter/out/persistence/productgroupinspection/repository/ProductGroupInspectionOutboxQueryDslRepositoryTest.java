package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.ProductGroupInspectionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
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
 * ProductGroupInspectionOutboxQueryDslRepositoryTest - 상품 그룹 검수 Outbox QueryDslRepository 통합 테스트.
 *
 * <p>상태 기반 필터 (PENDING, PROCESSING) 및 시간 조건 적용을 검증합니다.
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
@DisplayName("ProductGroupInspectionOutboxQueryDslRepository 통합 테스트")
class ProductGroupInspectionOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupInspectionOutboxQueryDslRepository repository() {
        return new ProductGroupInspectionOutboxQueryDslRepository(
                new JPAQueryFactory(entityManager));
    }

    private ProductGroupInspectionOutboxJpaEntity persist(
            ProductGroupInspectionOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태이고 createdAt이 beforeTime 이전인 Entity를 반환합니다")
        void findPendingOutboxes_WithPendingBeforeTime_ReturnsEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithProcessingEntity_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithCompletedEntity_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("FAILED 상태 Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithFailedEntity_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newFailedEntity());

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("최대 재시도 횟수에 도달한 PENDING Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithMaxRetryReached_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            // retryCount == maxRetry(3) 이므로 retryCount.lt(maxRetry) 조건 불만족
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntityWithRetry(3));

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("재시도 횟수가 남은 PENDING Entity는 조회됩니다")
        void findPendingOutboxes_WithRemainingRetry_ReturnsEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntityWithRetry(2));

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("limit 제한이 적용됩니다")
        void findPendingOutboxes_WithLimit_RespectsLimit() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity());
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity());
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 2);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("createdAt이 beforeTime 이후인 Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithFutureCreatedAt_ExcludesIt() {
            // given
            // beforeTime을 과거로 설정하여 현재 생성된 Entity가 조회 안되게 함
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity());
            Instant beforeTime = Instant.now().minusSeconds(60);

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("updatedAt이 timeoutThreshold 이전인 PROCESSING Entity를 반환합니다")
        void findProcessingTimeoutOutboxes_WithTimedOutEntity_ReturnsEntity() {
            // given
            Instant oldTime = Instant.now().minusSeconds(600);
            persist(
                    ProductGroupInspectionOutboxJpaEntityFixtures
                            .newProcessingEntityWithOldUpdatedAt(oldTime));
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("updatedAt이 timeoutThreshold 이후인 PROCESSING Entity는 조회되지 않습니다")
        void findProcessingTimeoutOutboxes_WithRecentEntity_ExcludesIt() {
            // given
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newProcessingEntity());
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태 Entity는 조회되지 않습니다")
        void findProcessingTimeoutOutboxes_WithPendingEntity_ExcludesIt() {
            // given
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity());
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity는 조회되지 않습니다")
        void findProcessingTimeoutOutboxes_WithCompletedEntity_ExcludesIt() {
            // given
            Instant oldTime = Instant.now().minusSeconds(600);
            persist(
                    ProductGroupInspectionOutboxJpaEntityFixtures
                            .newProcessingEntityWithOldUpdatedAt(oldTime));
            // COMPLETED 상태로 새로 생성
            persist(ProductGroupInspectionOutboxJpaEntityFixtures.newCompletedEntity());
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(timeoutThreshold, 10);

            // then - PROCESSING 타임아웃만 조회됨
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("limit 제한이 적용됩니다")
        void findProcessingTimeoutOutboxes_WithLimit_RespectsLimit() {
            // given
            Instant oldTime = Instant.now().minusSeconds(600);
            persist(
                    ProductGroupInspectionOutboxJpaEntityFixtures
                            .newProcessingEntityWithOldUpdatedAt(oldTime));
            persist(
                    ProductGroupInspectionOutboxJpaEntityFixtures
                            .newProcessingEntityWithOldUpdatedAt(oldTime));
            persist(
                    ProductGroupInspectionOutboxJpaEntityFixtures
                            .newProcessingEntityWithOldUpdatedAt(oldTime));
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ProductGroupInspectionOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(timeoutThreshold, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
