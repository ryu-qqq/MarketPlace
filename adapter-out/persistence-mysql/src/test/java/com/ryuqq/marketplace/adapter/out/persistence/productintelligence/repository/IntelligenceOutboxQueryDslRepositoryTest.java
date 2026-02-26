package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
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

/**
 * IntelligenceOutboxQueryDslRepositoryTest - Intelligence Pipeline Outbox QueryDslRepository 통합
 * 테스트.
 *
 * <p>PENDING 조회, 타임아웃 감지, 단건 조회 동작을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
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
@DisplayName("IntelligenceOutboxQueryDslRepository 통합 테스트")
class IntelligenceOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private IntelligenceOutboxQueryDslRepository repository() {
        return new IntelligenceOutboxQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
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
        @DisplayName("beforeTime 이전에 생성된 PENDING Outbox를 조회합니다")
        void findPendingOutboxes_WithOldPendingOutbox_ReturnsEntities() {
            // given
            persist(
                    ProductIntelligenceJpaEntityFixtures.oldPendingOutboxEntity(
                            null, 100L, "PI:100:1740556800000"));
            Instant beforeTime = Instant.now();

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(IntelligenceOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("beforeTime 이후에 생성된 PENDING Outbox는 조회하지 않습니다")
        void findPendingOutboxes_WithRecentPendingOutbox_ReturnsEmpty() {
            // given
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            null, 101L, "key-002"));
            Instant pastTime = Instant.now().minusSeconds(3600);

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findPendingOutboxes(pastTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태(SENT, COMPLETED, FAILED)는 조회하지 않습니다")
        void findPendingOutboxes_WithNonPendingOutboxes_ReturnsEmpty() {
            // given
            persist(
                    ProductIntelligenceJpaEntityFixtures.completedOutboxEntity(
                            null, 102L, "key-003"));
            persist(ProductIntelligenceJpaEntityFixtures.failedOutboxEntity(null, 103L, "key-004"));
            Instant beforeTime = Instant.now();

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit이 적용되어 지정한 수 이하로 반환합니다")
        void findPendingOutboxes_WithLimitApplied_ReturnsLimitedResults() {
            // given
            int limit = 2;
            for (int i = 0; i < 5; i++) {
                persist(
                        ProductIntelligenceJpaEntityFixtures.oldPendingOutboxEntity(
                                null, 200L + i, "key-limit-" + i));
            }
            Instant beforeTime = Instant.now();

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSizeLessThanOrEqualTo(limit);
        }
    }

    // ========================================================================
    // 2. findInProgressTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findInProgressTimeoutOutboxes")
    class FindInProgressTimeoutOutboxesTest {

        @Test
        @DisplayName("timeoutThreshold 이전에 업데이트된 SENT 상태 Outbox를 조회합니다")
        void findInProgressTimeoutOutboxes_WithTimedOutSentOutbox_ReturnsEntities() {
            // given
            persist(
                    ProductIntelligenceJpaEntityFixtures.timeoutSentOutboxEntity(
                            null, 300L, "key-timeout-001"));
            Instant threshold = Instant.now().minusSeconds(60);

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(threshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(IntelligenceOutboxJpaEntity.Status.SENT);
        }

        @Test
        @DisplayName("최근에 업데이트된 SENT 상태 Outbox는 조회하지 않습니다")
        void findInProgressTimeoutOutboxes_WithRecentSentOutbox_ReturnsEmpty() {
            // given
            persist(
                    ProductIntelligenceJpaEntityFixtures.sentOutboxEntity(
                            null, 301L, "key-timeout-002"));
            Instant threshold = Instant.now().minusSeconds(7200);

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(threshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("SENT가 아닌 상태는 조회하지 않습니다")
        void findInProgressTimeoutOutboxes_WithNonSentOutboxes_ReturnsEmpty() {
            // given
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            null, 302L, "key-timeout-003"));
            Instant threshold = Instant.now().minusSeconds(60);

            // when
            List<IntelligenceOutboxJpaEntity> result =
                    repository().findInProgressTimeoutOutboxes(threshold, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 IntelligenceOutbox를 단건 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            IntelligenceOutboxJpaEntity saved =
                    persist(
                            ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                                    null, 400L, "key-findbyid-001"));

            // when
            Optional<IntelligenceOutboxJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductGroupId()).isEqualTo(400L);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<IntelligenceOutboxJpaEntity> result = repository().findById(999999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("idempotencyKey가 올바르게 저장되어 조회됩니다")
        void findById_WithIdempotencyKey_ReturnsEntityWithCorrectKey() {
            // given
            String expectedKey = "unique-idempotency-key-for-test";
            IntelligenceOutboxJpaEntity saved =
                    persist(
                            ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                                    null, 401L, expectedKey));

            // when
            Optional<IntelligenceOutboxJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getIdempotencyKey()).isEqualTo(expectedKey);
        }
    }
}
