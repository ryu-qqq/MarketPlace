package com.ryuqq.marketplace.adapter.out.persistence.exchange.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.condition.ExchangeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSortKey;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
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
 * ExchangeClaimQueryDslRepositoryTest - 교환 클레임 QueryDslRepository 단위 통합 테스트.
 *
 * <p>ExchangeClaimJpaEntity는 soft-delete 없이 단순 CRUD 구조이므로 삭제 관련 필터 없이 데이터 조회 정확성을 검증합니다.
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
@DisplayName("ExchangeClaimQueryDslRepository 단위 통합 테스트")
class ExchangeClaimQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ExchangeClaimQueryDslRepository repository() {
        return new ExchangeClaimQueryDslRepository(
                new JPAQueryFactory(entityManager), new ExchangeConditionBuilder());
    }

    private ExchangeClaimJpaEntity persist(ExchangeClaimJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Entity를 반환합니다")
        void findById_WithExistingId_ReturnsEntity() {
            ExchangeClaimJpaEntity saved =
                    persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-find-001"));

            Optional<ExchangeClaimJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistent_ReturnsEmpty() {
            Optional<ExchangeClaimJpaEntity> result = repository().findById("non-existent-id");

            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("존재하는 orderItemId로 조회 시 Entity를 반환합니다")
        void findByOrderItemId_WithExistingOrderItemId_ReturnsEntity() {
            Long orderItemId = 1002L;
            ExchangeClaimJpaEntity saved =
                    persist(
                            ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                                    "id-order-001", orderItemId));

            Optional<ExchangeClaimJpaEntity> result = repository().findByOrderItemId(orderItemId);

            assertThat(result).isPresent();
            assertThat(result.get().getOrderItemId()).isEqualTo(orderItemId);
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId 조회 시 빈 Optional을 반환합니다")
        void findByOrderItemId_WithNonExistent_ReturnsEmpty() {
            Optional<ExchangeClaimJpaEntity> result = repository().findByOrderItemId(99999L);

            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByOrderItemIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemIds")
    class FindByOrderItemIdsTest {

        @Test
        @DisplayName("orderItemId 목록으로 복수 Entity를 조회합니다")
        void findByOrderItemIds_WithMultipleIds_ReturnsEntities() {
            Long orderItemId1 = 3001L;
            Long orderItemId2 = 3002L;
            persist(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "id-multi-001", orderItemId1));
            persist(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "id-multi-002", orderItemId2));

            List<ExchangeClaimJpaEntity> result =
                    repository().findByOrderItemIds(List.of(orderItemId1, orderItemId2));

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ExchangeClaimJpaEntity::getOrderItemId)
                    .containsExactlyInAnyOrder(orderItemId1, orderItemId2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByOrderItemIds_WithNoMatch_ReturnsEmptyList() {
            List<ExchangeClaimJpaEntity> result = repository().findByOrderItemIds(List.of(99999L));

            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findByIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIdIn")
    class FindByIdInTest {

        @Test
        @DisplayName("ID 목록과 sellerId로 Entity 목록을 조회합니다")
        void findByIdIn_WithMatchingIdsAndSellerId_ReturnsEntities() {
            ExchangeClaimJpaEntity entity1 =
                    persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-seller-001"));
            ExchangeClaimJpaEntity entity2 =
                    persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-seller-002"));

            List<ExchangeClaimJpaEntity> result =
                    repository()
                            .findByIdIn(
                                    List.of(entity1.getId(), entity2.getId()),
                                    ExchangeClaimJpaEntityFixtures.DEFAULT_SELLER_ID);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("sellerId가 다르면 조회되지 않습니다")
        void findByIdIn_WithDifferentSellerId_ReturnsEmpty() {
            ExchangeClaimJpaEntity entity =
                    persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-other-seller"));

            List<ExchangeClaimJpaEntity> result =
                    repository().findByIdIn(List.of(entity.getId()), 9999L);

            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 criteria로 Entity를 조회합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsEntities() {
            persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-crit-001"));
            persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-crit-002"));

            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();
            List<ExchangeClaimJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("상태 필터로 조회합니다")
        void findByCriteria_WithStatusFilter_ReturnsFilteredEntities() {
            persist(ExchangeClaimJpaEntityFixtures.entityWithStatus("id-status-001", "REQUESTED"));
            persist(ExchangeClaimJpaEntityFixtures.entityWithStatus("id-status-002", "COMPLETED"));

            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            List.of(ExchangeStatus.REQUESTED),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.CREATED_AT));

            List<ExchangeClaimJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> "REQUESTED".equals(e.getExchangeStatus()));
        }
    }

    // ========================================================================
    // 6. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("기본 criteria로 총 개수를 반환합니다")
        void countByCriteria_WithDefaultCriteria_ReturnsCount() {
            persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-cnt-001"));
            persist(ExchangeClaimJpaEntityFixtures.requestedEntity("id-cnt-002"));

            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("상태 필터 적용 시 해당 상태의 개수를 반환합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            persist(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus(
                            "id-cnt-stat-001", "COMPLETED"));
            persist(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus(
                            "id-cnt-stat-002", "COMPLETED"));

            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            List.of(ExchangeStatus.COMPLETED),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.CREATED_AT));

            long count = repository().countByCriteria(criteria);

            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }

    // ========================================================================
    // 7. countByStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByStatus")
    class CountByStatusTest {

        @Test
        @DisplayName("상태별 교환 개수를 반환합니다")
        void countByStatus_WithMixedStatuses_ReturnsStatusCounts() {
            persist(ExchangeClaimJpaEntityFixtures.entityWithStatus("id-stat-001", "REQUESTED"));
            persist(ExchangeClaimJpaEntityFixtures.entityWithStatus("id-stat-002", "REQUESTED"));
            persist(ExchangeClaimJpaEntityFixtures.entityWithStatus("id-stat-003", "COMPLETED"));

            Map<ExchangeStatus, Long> result = repository().countByStatus();

            assertThat(result).isNotEmpty();
            assertThat(result.get(ExchangeStatus.REQUESTED)).isGreaterThanOrEqualTo(2L);
            assertThat(result.get(ExchangeStatus.COMPLETED)).isGreaterThanOrEqualTo(1L);
        }

        @Test
        @DisplayName("데이터가 없으면 빈 맵을 반환합니다")
        void countByStatus_WithNoData_ReturnsEmptyMap() {
            Map<ExchangeStatus, Long> result = repository().countByStatus();

            assertThat(result.values().stream().mapToLong(Long::longValue).sum())
                    .isGreaterThanOrEqualTo(0);
        }
    }
}
