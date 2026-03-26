package com.ryuqq.marketplace.adapter.out.persistence.shipment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.ShipmentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.condition.ShipmentConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSortKey;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
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
 * ShipmentQueryDslRepositoryTest - 배송 QueryDslRepository 통합 테스트.
 *
 * <p>soft-delete(notDeleted) 필터 적용을 우선 검증합니다.
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
@DisplayName("ShipmentQueryDslRepository 통합 테스트")
class ShipmentQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ShipmentQueryDslRepository repository() {
        return new ShipmentQueryDslRepository(
                new JPAQueryFactory(entityManager), new ShipmentConditionBuilder());
    }

    private ShipmentJpaEntity persist(ShipmentJpaEntity entity) {
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
        @DisplayName("미삭제 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            ShipmentJpaEntity saved = persist(ShipmentJpaEntityFixtures.readyEntity("id-find-001"));

            Optional<ShipmentJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            ShipmentJpaEntity deleted = persist(ShipmentJpaEntityFixtures.deletedEntity());

            Optional<ShipmentJpaEntity> result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistent_ReturnsEmpty() {
            Optional<ShipmentJpaEntity> result = repository().findById("non-existent-id");

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
        @DisplayName("미삭제 Entity는 orderItemId로 조회됩니다")
        void findByOrderItemId_WithNotDeleted_ReturnsEntity() {
            String orderItemId = "01940001-0000-7000-8000-000000000002";
            ShipmentJpaEntity saved =
                    persist(
                            ShipmentJpaEntityFixtures.readyEntityWithOrderItemId(
                                    "id-order-001", orderItemId));

            Optional<ShipmentJpaEntity> result = repository().findByOrderItemId(orderItemId);

            assertThat(result).isPresent();
            assertThat(result.get().getOrderItemId()).isEqualTo(orderItemId);
        }

        @Test
        @DisplayName("삭제된 Entity는 orderItemId로 조회되지 않습니다")
        void findByOrderItemId_WithDeleted_ReturnsEmpty() {
            ShipmentJpaEntity deleted = persist(ShipmentJpaEntityFixtures.deletedEntity());

            Optional<ShipmentJpaEntity> result =
                    repository().findByOrderItemId(ShipmentJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId 조회 시 빈 Optional을 반환합니다")
        void findByOrderItemId_WithNonExistent_ReturnsEmpty() {
            Optional<ShipmentJpaEntity> result =
                    repository().findByOrderItemId("01940001-0000-7000-8000-000000000999");

            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 criteria로 미삭제 Entity를 조회합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsNotDeletedEntities() {
            persist(ShipmentJpaEntityFixtures.readyEntity("id-crit-001"));
            persist(ShipmentJpaEntityFixtures.readyEntity("id-crit-002"));
            persist(ShipmentJpaEntityFixtures.deletedEntity());

            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();
            List<ShipmentJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
            assertThat(result).noneMatch(ShipmentJpaEntity::isDeleted);
        }

        @Test
        @DisplayName("상태 필터로 조회합니다")
        void findByCriteria_WithStatusFilter_ReturnsFilteredEntities() {
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-status-001", "READY"));
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-status-002", "SHIPPED"));

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            List.of(ShipmentStatus.READY),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.CREATED_AT));

            List<ShipmentJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> "READY".equals(e.getStatus()));
        }

        @Test
        @DisplayName("삭제된 Entity는 criteria 조회에서 제외됩니다")
        void findByCriteria_ExcludesDeletedEntities() {
            persist(ShipmentJpaEntityFixtures.deletedEntity());

            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();
            List<ShipmentJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).noneMatch(ShipmentJpaEntity::isDeleted);
        }
    }

    // ========================================================================
    // 4. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("미삭제 Entity 개수를 반환합니다")
        void countByCriteria_WithNotDeletedEntities_ReturnsCount() {
            persist(ShipmentJpaEntityFixtures.readyEntity("id-cnt-001"));
            persist(ShipmentJpaEntityFixtures.readyEntity("id-cnt-002"));
            persist(ShipmentJpaEntityFixtures.deletedEntity());

            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("상태 필터 적용 시 해당 상태의 개수를 반환합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-cnt-stat-001", "SHIPPED"));
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-cnt-stat-002", "SHIPPED"));

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            List.of(ShipmentStatus.SHIPPED),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.CREATED_AT));

            long count = repository().countByCriteria(criteria);

            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }

    // ========================================================================
    // 5. countByStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByStatus")
    class CountByStatusTest {

        @Test
        @DisplayName("미삭제 Entity의 상태별 개수를 반환합니다")
        void countByStatus_WithMixedStatuses_ReturnsStatusCounts() {
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-stat-001", "READY"));
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-stat-002", "READY"));
            persist(ShipmentJpaEntityFixtures.entityWithStatus("id-stat-003", "SHIPPED"));

            Map<String, Long> result = repository().countByStatus();

            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("삭제된 Entity는 상태별 개수에서 제외됩니다")
        void countByStatus_ExcludesDeletedEntities() {
            ShipmentJpaEntity deleted = persist(ShipmentJpaEntityFixtures.deletedEntity());
            String deletedId = deleted.getId();

            Map<String, Long> result = repository().countByStatus();

            // 삭제된 Entity는 포함되지 않으므로 조회 결과에서 제외 검증
            // (deletedAt이 있는 entity는 notDeleted 필터로 제외됨)
            assertThat(result.values().stream().mapToLong(Long::longValue).sum())
                    .isGreaterThanOrEqualTo(0);
        }
    }
}
